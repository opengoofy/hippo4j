import { theme } from 'antd';
import { lightDefaultTheme } from '.';
export const defaultAlgorithm = {
  token: {
    colorPrimary: lightDefaultTheme.primary,
    fontSize: 14,
    fontSizeHeading1: 18,
  },
  components: {
    Layout: {
      bodyBg: lightDefaultTheme.backgroundColor.bg1,
      headerBg: lightDefaultTheme.backgroundColor.bgHeader,
      triggerBg: lightDefaultTheme.backgroundColor.bg1,
      triggerColor: lightDefaultTheme.fontColor.fc1,
    },
    Button: {
      // fontSize: 14,
    },
    Table: {
      // padding: 10,
      // paddingXS: 5,
      // margin: 0,
      // cellFontSize: 12,
      // colorBorderSecondary: lightDefaultTheme.borderColor.bl1,
      // paddingContentVerticalLG: 4,
    },
    Modal: {
      // borderRadiusLG: 2,
      // borderRadiusSM: 2,
      // colorText: lightDefaultTheme.fontColor.fc3,
      // borderRadius: 2,
      // paddingContentHorizontalLG: 0,
      // paddingMD: 0,
    },
    Menu: {
      itemBg: lightDefaultTheme.backgroundColor.bg1,
      // itemSelectedBg: lightDefaultTheme.primary,
      // itemSelectedColor: lightDefaultTheme.fontColor.fc1,
      activeBarWidth: 0,
      activeBarHeight: 0,
      activeBarBorderWidth: 0,
      // subMenuItemBorderRadius: 8,
      // horizontalItemBorderRadius: 8,
      // itemBorderRadius: 8,
    },
  },
  algorithm: theme.defaultAlgorithm,
};
