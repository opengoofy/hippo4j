import React, { Suspense } from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { BrowserRouter } from 'react-router-dom';
import { MyStore } from './context';
import './config/i18n';
import 'antd/dist/reset.css';
import { Spin } from 'antd';

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

root.render(
  <React.StrictMode>
    <BrowserRouter>
      {/* theme context */}
      <MyStore>
        {/* theme config context */}
        <Suspense fallback={<Spin />}>
          <App />
        </Suspense>
      </MyStore>
    </BrowserRouter>
  </React.StrictMode>
);
