import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { BrowserRouter } from 'react-router-dom';
import { ThemeStore } from './context/themeContext';
import ThemeComponent from './components/theme-com';
import 'antd/dist/reset.css';

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

root.render(
  <React.StrictMode>
    <BrowserRouter>
      {/* 全局配置主体context */}
      <ThemeStore>
        {/* 提供主体色配置 */}
        <ThemeComponent>
          <App />
        </ThemeComponent>
      </ThemeStore>
    </BrowserRouter>
  </React.StrictMode>
);
