import { theme } from 'antd';
import { darkDefaultTheme } from '.';
export const darkAlgorithm = {
  token: {
    borderRadius: 6,
    colorPrimary: darkDefaultTheme.primary,
    fontSize: 14,
  },
  components: {
    Layout: {
      bodyBg: darkDefaultTheme.backgroundColor.bg1,
      headerBg: darkDefaultTheme.backgroundColor.bgHeader,
    },
    Button: {
      fontSize: 14,
    },
    Table: {
      // borderRadius: 0,
      // borderRadiusLG: 0,
      padding: 10,
      paddingXS: 5,
      margin: 0,
      fontSize: 14,
      colorBorderSecondary: darkDefaultTheme.borderColor.bl1,
      paddingContentVerticalLG: 4,
    },
    Modal: {
      borderRadiusLG: 2,
      borderRadiusSM: 2,
      colorText: darkDefaultTheme.fontColor.fc3,
      borderRadius: 2,
      paddingContentHorizontalLG: 0,
      paddingMD: 0,
    },
    Menu: {
      itemBg: darkDefaultTheme.backgroundColor.bg1,
      activeBarWidth: 0,
      activeBarHeight: 0,
      activeBarBorderWidth: 0,
      subMenuItemBorderRadius: 8,
      horizontalItemBorderRadius: 8,
      itemBorderRadius: 8,
    },
  },
  algorithm: theme.darkAlgorithm,
};
