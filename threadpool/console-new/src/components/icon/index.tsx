import { createFromIconfontCN } from '@ant-design/icons';

interface Props {
  type: string;
}

const MyIcon = createFromIconfontCN({
  scriptUrl: '//at.alicdn.com/t/c/font_4254722_1xl4w1k5c53.js', // 在 iconfont.cn 上生成
});

const IconFont = (props: Props) => {
  return <MyIcon {...props}></MyIcon>;
};

export default IconFont;
