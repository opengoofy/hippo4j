import { theme } from 'antd';
import { darkDefaultTheme } from '.';
export const darkAlgorithm = {
  token: {
    colorPrimary: darkDefaultTheme.primary,
    fontSize: 14,
    fontSizeHeading1: 18,
  },
  components: {
    Layout: {
      bodyBg: darkDefaultTheme.backgroundColor.bg1,
      headerBg: darkDefaultTheme.backgroundColor.bgHeader,
      triggerBg: darkDefaultTheme.backgroundColor.bg1,
      triggerColor: darkDefaultTheme.fontColor.fc1,
    },
    Button: {
      // fontSize: 14,
    },
    Table: {
      // borderRadius: 0,
      // borderRadiusLG: 0,
      // padding: 10,
      // paddingXS: 5,
      // margin: 0,
      // cellFontSize: 12,
      // colorBorderSecondary: darkDefaultTheme.borderColor.bl1,
      // paddingContentVerticalLG: 4,
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
      // itemSelectedBg: darkDefaultTheme.primary,
      // itemSelectedColor: darkDefaultTheme.fontColor.fc1,
      activeBarWidth: 0,
      activeBarHeight: 0,
      activeBarBorderWidth: 0,
      // subMenuItemBorderRadius: 8,
      // horizontalItemBorderRadius: 8,
      // itemBorderRadius: 8,
    },
  },
  algorithm: theme.darkAlgorithm,
};
