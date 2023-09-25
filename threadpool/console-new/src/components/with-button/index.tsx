import { Button, ButtonProps } from 'antd';
import React from 'react';
import { SearchOutlined, PlusOutlined, RedoOutlined } from '@ant-design/icons';

function withSearchIconButton<P extends ButtonProps>(WrappedComponent: React.ComponentType<P>) {
  return function EnhancedComponent(props: P) {
    return <WrappedComponent icon={<SearchOutlined></SearchOutlined>} {...props} />;
  };
}

function withAddIconButton<P extends ButtonProps>(WrappedComponent: React.ComponentType<P>) {
  return function EnhancedComponent(props: P) {
    return <WrappedComponent icon={<PlusOutlined />} {...props}></WrappedComponent>;
  };
}

function withResetIconButton<P extends ButtonProps>(WrappedComponent: React.ComponentType<P>) {
  return function EnhancedComponent(props: P) {
    return <WrappedComponent {...props} icon={<RedoOutlined />}></WrappedComponent>;
  };
}

export const SearchButton = withSearchIconButton(Button);

export const AddButton = withAddIconButton(Button);

export const ResetButton = withResetIconButton(Button);
