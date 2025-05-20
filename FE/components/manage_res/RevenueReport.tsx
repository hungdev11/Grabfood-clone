import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

interface RevenueData {
  label: string;
  totalOrders: number;
  grossRevenue: number;
  netRevenue: number;
}

const RevenueReport = () => {
  const [revenueData, setRevenueData] = useState<RevenueData[]>([]);
  const [restaurantId, setRestaurantId] = useState(1);
  const [dateFrom, setDateFrom] = useState('2025-01-01');
  const [dateTo, setDateTo] = useState('2025-12-31');
  const [groupBy, setGroupBy] = useState('daily');

  const fetchRevenueStats = async () => {
    try {
      const response = await axios.get(`http://localhost:6969/grab/report/revenue`, {
        params: { restaurantId, dateFrom, dateTo, groupBy },
      });
      setRevenueData(response.data.data);
    } catch (error) {
      console.error('Error fetching revenue stats:', error);
    }
  };

  useEffect(() => {
    fetchRevenueStats();
  }, [restaurantId, dateFrom, dateTo, groupBy]);

  return (
    <div className="p-6 bg-white rounded-xl shadow-md max-w-6xl mx-auto mt-6">
      <h1 className="text-2xl font-bold mb-4 text-gray-700">📊 Revenue Report</h1>
        <div style={{ marginBottom: '1rem' }}>
            <label className="mr-4">
                From:{' '}
                <input
                type="date"
                value={dateFrom}
                onChange={(e) => setDateFrom(e.target.value)}
                />
            </label>{' '}
            <label>
                To:{' '}
                <input
                type="date"
                value={dateTo}
                onChange={(e) => setDateTo(e.target.value)}
                />
            </label>{' '}
            </div>

      <div className="flex items-center gap-4 mb-6">
        <label className="font-medium">Group By:</label>
        <select
          className="px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-green-400"
          value={groupBy}
          onChange={(e) => setGroupBy(e.target.value)}
        >
          <option value="day">Daily</option>
          <option value="week">Weekly</option>
          <option value="month">Monthly</option>
          <option value="quarter">Quarterly</option>
          <option value="year">Yearly</option>
        </select>
      </div>

      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={revenueData} margin={{ top: 10, right: 30, left: 0, bottom: 10 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="label" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line type="monotone" dataKey="grossRevenue" stroke="#8884d8" name="Gross Revenue" />
          {/* <Line type="monotone" dataKey="netRevenue" stroke="#82ca9d" name="Net Revenue" /> */}
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default RevenueReport;
