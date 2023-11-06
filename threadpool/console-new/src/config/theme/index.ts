import { DefaultTheme } from 'styled-components';

export const lightDefaultTheme: DefaultTheme = {
  primary: '#1890ff',
  baseColor: {
    // 前两个是固定的，用于，有颜色按钮 字体颜色等固定不会变的颜色值
    bc1: '#fff',
    bc2: '#000000',
    bc3: '#ED2D00',
    bc4: '#10CC55',
    bc5: '#3094f1',
  },
  fontColor: {
    fc1: '#333',
    fc2: '#000',
    fc3: '#666',
    fc4: '#D2E0F4',
    fc5: '#000000',
    fc6: '#FFFFFF',
  },
  borderColor: {
    bl1: '#E2E2E2',
    bl2: '#d8dbe2',
    bl3: '#B5BDCE',
  },
  backgroundColor: {
    bgHeader: '#fff',
    bgContent: '#fff',
    // main bgc
    bg1: '#ebebf2',
    // app or head bgc
    bg2: '#fff',
    // title的及表格头部背景极
    bg3: '#EDEDED',
    bg5: '#F8F8F8',
    bg4: '#F6F6F6',
    bg6: 'rgba(0, 0, 0, 0.70)',
  },
  hoverColor: {
    hc1: 'rgba(24, 144, 255, 0)',
    // 表格的hover及选 中
    hc2: '#F5F8FA',
  },
};

export const darkDefaultTheme: DefaultTheme = {
  primary: '#0d68a8',
  baseColor: {
    bc1: '#fff',
    bc2: '#000000',
    bc3: '#FF3D3D',
    bc4: '#10CC55',
    bc5: '#3094f1',
    // 固定为白色，有颜色按钮文字颜色  fixedcolor
  },
  fontColor: {
    fc1: '#ffffff',
    fc2: '#B4B6B8',
    fc3: '#555555',
    fc4: '#666666',
    fc5: '#FFFFFF',
    fc6: '#FFFFFF',
  },
  borderColor: {
    bl1: '#6A6A6A',
    bl2: '#4A4B51',
    bl3: '#424242',
  },
  backgroundColor: {
    bgHeader: '#141414',
    bgContent: '#141414',
    // 大面积色
    bg1: '#141414',
    // tab顶部颜色
    bg2: '#323337',
    // title 颜色
    bg3: '#2A2B2E',
    bg5: '#4A4B51',
    // 菜单选中
    bg4: '#0F3C66',
    //  锁定背景色
    bg6: 'rgba(255, 255, 255, 0.70)',
    // bg3: "rgba(255, 255, 255, 0.70)",
    // bg4: "rgba(250, 250, 250, 1)",
  },
  hoverColor: {
    hc1: 'rgba(24, 144, 255, 0)',
    hc2: '#2A2B2E',
  },
};
