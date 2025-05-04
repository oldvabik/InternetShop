import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import ProductsPage from './pages/ProductsPage';
import CategoriesPage from './pages/CategoriesPage';
import OrdersPage from './pages/OrderPage';
import { Layout, Menu } from 'antd';
import { Link } from 'react-router-dom';

const { Header, Content } = Layout;

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
          flex: '0 0 auto'
        }}>
          <Menu 
            theme="dark" 
            mode="horizontal"
            style={{
              lineHeight: '64px',
              display: 'flex',
              justifyContent: 'flex-start',
              paddingLeft: '24px'
            }}
          >
            <Menu.Item key="products">
              <Link to="/products">Продукты</Link>
            </Menu.Item>
            <Menu.Item key="categories">
              <Link to="/categories">Категории</Link>
            </Menu.Item>
            <Menu.Item key="orders">
              <Link to="/orders">Заказы</Link>
            </Menu.Item>
          </Menu>
        </Header>
        <Content style={{ 
          padding: '24px 50px', 
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