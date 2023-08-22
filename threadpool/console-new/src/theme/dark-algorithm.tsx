import { theme } from 'antd';
import { darkDefaultTheme } from '.';
export const darkAlgorithm = {
  token: {
    borderRadius: 2,
    colorPrimary: darkDefaultTheme.primary,
    fontSize: 14,
  },
  components: {
    Button: {
      fontSize: 14,
    },
    Table: {
      borderRadius: 0,
      borderRadiusLG: 0,
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
  },
  algorithm: theme.darkAlgorithm,
};
