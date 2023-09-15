import 'styled-components';
// and extend them!
declare module 'styled-components' {
  export interface DefaultTheme {
    primary: string;

    // 基本固定颜色，如有颜色按钮，文字颜色
    baseColor: {
      bc1: string;
      bc2: string;
      bc3: string;
      bc4: string;
      bc5: string;
    };
    // 字体颜色
    fontColor: {
      fc1: string;
      fc2: string;
      fc3: string;
      fc4: string;
      fc5: string;
      fc6: string;
    };
    // 边框颜色 line
    borderColor: {
      bl1: string;
      bl2: string;
      bl3: string;
    };
    // 背景色
    backgroundColor: {
      bgHeader: string;
      bgContent: string;
      bg1: string;
      bg2: string;
      bg3: string;
      bg4: string;
      bg5: string;
      bg6: string;
    };
    hoverColor: {
      hc1: string;
      hc2: string;
    };
  }
}
