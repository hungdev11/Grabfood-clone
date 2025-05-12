CREATE DEFINER=`root`@`%` PROCEDURE `grab-food`.`get_all_orders_of_restaurant`(IN restaurant_id INT)
BEGIN
    SELECT DISTINCT cd.order_id
    FROM `grab-food`.cart_detail cd
    JOIN `grab-food`.food f ON cd.food_id = f.id
    WHERE f.restaurant_id = restaurant_id;
END;

CREATE PROCEDURE `grab-food`.sp_get_revenue_stats(
    IN p_restaurant_id INT,
    IN p_date_from DATE,
    IN p_date_to DATE,
    IN p_group_by VARCHAR(10)
)
BEGIN
    WITH revenue_base AS (
        SELECT
            o.id AS order_id,
            o.order_date,
            cd.quantity,
            fd.price AS main_price,
            v.type AS voucher_type,
            v.value AS voucher_value,
            cd.ids AS topping_ids
        FROM `grab-food`.orders o
        JOIN `grab-food`.cart_detail cd ON o.id = cd.order_id
        JOIN `grab-food`.food f ON cd.food_id = f.id
        JOIN `grab-food`.food_detail fd ON fd.food_id = f.id
            AND (
                (fd.end_time IS NULL AND o.order_date >= fd.start_time)
                OR (o.order_date BETWEEN fd.start_time AND fd.end_time)
            )
        LEFT JOIN `grab-food`.voucher_detail vd ON vd.food_id = f.id
            AND o.order_date BETWEEN vd.start_date AND vd.end_date
        LEFT JOIN `grab-food`.voucher v ON v.id = vd.voucher_id
            AND v.restaurant_id = f.restaurant_id
        WHERE f.restaurant_id = p_restaurant_id
          AND o.order_date BETWEEN p_date_from AND p_date_to
          AND o.status = 'COMPLETED'
    ),
    toppings_expanded AS (
        SELECT
            rb.order_id,
            rb.order_date,
            rb.quantity,
            rb.main_price,
            rb.voucher_type,
            rb.voucher_value,
            f2.id AS topping_id,
            fd2.price AS topping_price,
            v2.type AS topping_voucher_type,
            v2.value AS topping_voucher_value
        FROM revenue_base rb
        LEFT JOIN JSON_TABLE(
            CONCAT('[\"', REPLACE(rb.topping_ids, ',', '\",\"'), '\"]'),
            "$[*]" COLUMNS (topping_id INT PATH "$")
        ) AS jt ON TRUE
        LEFT JOIN `grab-food`.food f2 ON f2.id = jt.topping_id
        LEFT JOIN `grab-food`.food_detail fd2 ON fd2.food_id = f2.id
            AND (
                (fd2.end_time IS NULL AND rb.order_date >= fd2.start_time)
                OR (rb.order_date BETWEEN fd2.start_time AND fd2.end_time)
            )
        LEFT JOIN `grab-food`.voucher_detail vd2 ON vd2.food_id = f2.id
            AND rb.order_date BETWEEN vd2.start_date AND vd2.end_date
        LEFT JOIN `grab-food`.voucher v2 ON v2.id = vd2.voucher_id
            AND v2.restaurant_id = f2.restaurant_id
    ),
    revenue_calculated AS (
        SELECT
            te.order_id,
            te.order_date,
            te.quantity,
            te.main_price,
            te.voucher_type,
            te.voucher_value,
            te.topping_price,
            te.topping_voucher_type,
            te.topping_voucher_value,
            -- Tính doanh thu gộp cho món chính
            te.main_price * te.quantity AS main_gross,
            -- Tính doanh thu gộp cho toppings
            IFNULL(te.topping_price * te.quantity, 0) AS topping_gross,
            -- Tính doanh thu thực tế cho món chính sau khi áp dụng voucher
            CASE
                WHEN te.voucher_type = 'PERCENTAGE' THEN
                    GREATEST(0, te.main_price * (1 - LEAST(te.voucher_value, 100) / 100) * te.quantity)
                WHEN te.voucher_type = 'FIXED' THEN
                    GREATEST(0, (te.main_price - te.voucher_value) * te.quantity)
                ELSE
                    te.main_price * te.quantity
            END AS main_net,
            -- Tính doanh thu thực tế cho toppings sau khi áp dụng voucher
            CASE
                WHEN te.topping_voucher_type = 'PERCENTAGE' THEN
                    GREATEST(0, te.topping_price * (1 - LEAST(te.topping_voucher_value, 100) / 100) * te.quantity)
                WHEN te.topping_voucher_type = 'FIXED' THEN
                    GREATEST(0, (te.topping_price - te.topping_voucher_value) * te.quantity)
                ELSE
                    IFNULL(te.topping_price * te.quantity, 0)
            END AS topping_net
        FROM toppings_expanded te
    )
    SELECT
        CASE
            WHEN p_group_by = 'day' THEN DATE(rc.order_date)
            WHEN p_group_by = 'week' THEN STR_TO_DATE(CONCAT(YEAR(rc.order_date), ' ', WEEK(rc.order_date, 1), ' Sunday'), '%X %V %W')
            WHEN p_group_by = 'month' THEN DATE_FORMAT(rc.order_date, '%Y-%m-01')
            WHEN p_group_by = 'quarter' THEN STR_TO_DATE(CONCAT(YEAR(rc.order_date), '-', LPAD(((QUARTER(rc.order_date) - 1) * 3 + 1), 2, '0'), '-01'), '%Y-%m-%d')
            WHEN p_group_by = 'year' THEN DATE_FORMAT(rc.order_date, '%Y-01-01')
            ELSE DATE(rc.order_date)
        END AS label,
        COUNT(DISTINCT rc.order_id) AS total_orders,
        SUM(rc.main_gross + rc.topping_gross) AS gross_revenue,
        SUM(rc.main_net + rc.topping_net) AS net_revenue
    FROM revenue_calculated rc
    GROUP BY label
    ORDER BY label;
END;

CREATE PROCEDURE `grab-food`.sp_get_yearly_monthly_revenue(
    IN p_restaurant_id INT,
    IN p_year INT
)
BEGIN
    WITH base_data AS (
        SELECT
            o.id AS order_id,
            o.order_date,
            MONTH(o.order_date) AS order_month,
            cd.quantity,
            fd.price AS main_price,
            cd.ids AS topping_ids,
            v.type AS voucher_type,
            v.value AS voucher_value
        FROM `grab-food`.orders o
        JOIN `grab-food`.cart_detail cd ON o.id = cd.order_id
        JOIN `grab-food`.food f ON cd.food_id = f.id
        JOIN `grab-food`.food_detail fd ON fd.food_id = f.id
            AND (
                (fd.end_time IS NULL AND o.order_date >= fd.start_time)
                OR (o.order_date BETWEEN fd.start_time AND fd.end_time)
            )
        LEFT JOIN `grab-food`.voucher_detail vd ON vd.food_id = f.id
            AND o.order_date BETWEEN vd.start_date AND vd.end_date
        LEFT JOIN `grab-food`.voucher v ON v.id = vd.voucher_id
            AND v.restaurant_id = f.restaurant_id
        WHERE f.restaurant_id = p_restaurant_id
          AND YEAR(o.order_date) = p_year
          AND o.status = 'COMPLETED'
    ),
    topping_data AS (
        SELECT
            bd.order_id,
            bd.order_month,
            bd.quantity,
            bd.main_price,
            bd.voucher_type,
            bd.voucher_value,
            f2.id AS topping_id,
            fd2.price AS topping_price,
            v2.type AS topping_voucher_type,
            v2.value AS topping_voucher_value
        FROM base_data bd
        LEFT JOIN JSON_TABLE(
            CONCAT('[\"', REPLACE(bd.topping_ids, ',', '\",\"'), '\"]'),
            "$[*]" COLUMNS (topping_id INT PATH "$")
        ) jt ON TRUE
        LEFT JOIN `grab-food`.food f2 ON f2.id = jt.topping_id
        LEFT JOIN `grab-food`.food_detail fd2 ON fd2.food_id = f2.id
            AND (
                (fd2.end_time IS NULL AND bd.order_date >= fd2.start_time)
                OR (bd.order_date BETWEEN fd2.start_time AND fd2.end_time)
            )
        LEFT JOIN `grab-food`.voucher_detail vd2 ON vd2.food_id = f2.id
            AND bd.order_date BETWEEN vd2.start_date AND vd2.end_date
        LEFT JOIN `grab-food`.voucher v2 ON v2.id = vd2.voucher_id
            AND v2.restaurant_id = f2.restaurant_id
    ),
    revenue AS (
        SELECT
            td.order_month,
            td.order_id,
            -- Gross revenue
            td.main_price * td.quantity AS main_gross,
            IFNULL(td.topping_price * td.quantity, 0) AS topping_gross,
            -- Net revenue
            CASE
                WHEN td.voucher_type = 'PERCENTAGE' THEN
                    GREATEST(0, td.main_price * (1 - LEAST(td.voucher_value, 100) / 100) * td.quantity)
                WHEN td.voucher_type = 'FIXED' THEN
                    GREATEST(0, (td.main_price - td.voucher_value) * td.quantity)
                ELSE
                    td.main_price * td.quantity
            END AS main_net,
            CASE
                WHEN td.topping_voucher_type = 'PERCENTAGE' THEN
                    GREATEST(0, td.topping_price * (1 - LEAST(td.topping_voucher_value, 100) / 100) * td.quantity)
                WHEN td.topping_voucher_type = 'FIXED' THEN
                    GREATEST(0, (td.topping_price - td.topping_voucher_value) * td.quantity)
                ELSE
                    IFNULL(td.topping_price * td.quantity, 0)
            END AS topping_net
        FROM topping_data td
    )
    SELECT
        m.month AS label,
        COUNT(DISTINCT r.order_id) AS total_orders,
        IFNULL(SUM(r.main_gross + r.topping_gross), 0) AS gross_revenue,
        IFNULL(SUM(r.main_net + r.topping_net), 0) AS net_revenue
    FROM (
        SELECT 1 AS month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
        UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8
        UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12
    ) m
    LEFT JOIN revenue r ON r.order_month = m.month
    GROUP BY m.month
    ORDER BY m.month;

END;