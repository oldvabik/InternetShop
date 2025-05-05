import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import ProductsPage from './pages/ProductsPage';
import CategoriesPage from './pages/CategoriesPage';
import OrdersPage from './pages/OrderPage';
import { Layout, Menu, Typography, Grid } from 'antd';
import { Link } from 'react-router-dom';

const { Header, Content } = Layout;
const { Text } = Typography;
const { useBreakpoint } = Grid;

const App: React.FC = () => {
  const screens = useBreakpoint();

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
          paddingLeft: screens.xs ? '8px' : '16px',
          paddingRight: screens.xs ? '8px' : '16px'
        }}>
          <div style={{
            color: 'white',
            fontWeight: 'bold',
            fontSize: screens.xs ? '14px' : '16px',
            marginRight: screens.xs ? '16px' : '32px',
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
            <Menu.Item key="products" style={{ padding: screens.xs ? '0 8px' : '0 16px' }}>
              <Link to="/products">{screens.xs ? 'Товары' : 'Продукты'}</Link>
            </Menu.Item>
            <Menu.Item key="categories" style={{ padding: screens.xs ? '0 8px' : '0 16px' }}>
              <Link to="/categories">Категории</Link>
            </Menu.Item>
            <Menu.Item key="orders" style={{ padding: screens.xs ? '0 8px' : '0 16px' }}>
              <Link to="/orders">Заказы</Link>
            </Menu.Item>
          </Menu>
        </Header>
        <Content style={{ 
          padding: screens.xs ? '12px' : '24px', 
          marginTop: screens.xs ? 8 : 16,
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