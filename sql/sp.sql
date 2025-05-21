CREATE DEFINER=`root`@`%` PROCEDURE `grab-food`.`sp_get_revenue_stats`(
    IN p_restaurant_id INT,
    IN p_date_from DATE,
    IN p_date_to DATE,
    IN p_group_by VARCHAR(10)
)
BEGIN
    SELECT
    CASE
        WHEN p_group_by = 'day' THEN DATE(o.order_date)
        WHEN p_group_by = 'week' THEN STR_TO_DATE(CONCAT(YEAR(o.order_date), ' ', WEEK(o.order_date, 1), ' Sunday'), '%X %V %W')
        WHEN p_group_by = 'month' THEN DATE_FORMAT(o.order_date, '%Y-%m-01')
        WHEN p_group_by = 'quarter' THEN STR_TO_DATE(CONCAT(YEAR(o.order_date), '-', LPAD(((QUARTER(o.order_date) - 1) * 3 + 1), 2, '0'), '-01'), '%Y-%m-%d')
        WHEN p_group_by = 'year' THEN DATE_FORMAT(o.order_date, '%Y-01-01')
        ELSE DATE(o.order_date)
    END AS label,
    COUNT(*) AS total_orders,
    SUM(o.total_price) AS gross_revenue
	FROM `grab-food`.orders o
	WHERE 
	  o.order_date BETWEEN p_date_from AND p_date_to
	  AND o.status = 'COMPLETED'
	GROUP BY label
	ORDER BY label;

END
