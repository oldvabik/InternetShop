import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import ProductsPage from './pages/ProductsPage';
import CategoriesPage from './pages/CategoriesPage';
import OrdersPage from './pages/OrderPage';
import { Layout, Menu, Typography } from 'antd';
import { Link } from 'react-router-dom';

const { Header, Content } = Layout;
const { Text } = Typography;

const App: React.FC = () => {
  return (
    <Router>
      <Layout style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
        <Header style={{
          position: 'sticky',
          top: 0,
          zIndex: 1,
          width: '100%',
          padding: 0,
          flex: '0 0 auto',
          display: 'flex',
          alignItems: 'center',
          paddingLeft: '16px'
        }}>
          <div style={{
            color: 'white',
            fontWeight: 'bold',
            fontSize: '16px',
            marginRight: '32px',
            whiteSpace: 'nowrap'
          }}>
            <Text style={{ color: 'white' }}>Складской учёт</Text>
          </div>
          <Menu 
            theme="dark" 
            mode="horizontal"
            style={{
              lineHeight: '64px',
              flex: 1,
              borderBottom: 'none',
              background: 'transparent',
              minWidth: 0
            }}
          >
            <Menu.Item key="products" style={{ padding: '0 16px' }}>
              <Link to="/products">Продукты</Link>
            </Menu.Item>
            <Menu.Item key="categories" style={{ padding: '0 16px' }}>
              <Link to="/categories">Категории</Link>
            </Menu.Item>
            <Menu.Item key="orders" style={{ padding: '0 16px' }}>
              <Link to="/orders">Заказы</Link>
            </Menu.Item>
          </Menu>
        </Header>
        <Content style={{ 
          padding: '24px', 
          marginTop: 16,
          flex: '1 0 auto',
          overflow: 'hidden'
        }}>
          <Routes>
            <Route path="/products" element={<ProductsPage />} />
            <Route path="/categories" element={<CategoriesPage />} />
            <Route path="/orders" element={<OrdersPage />} />
            <Route path="/" element={<ProductsPage />} />
          </Routes>
        </Content>
      </Layout>
    </Router>
  );
};

export default App;