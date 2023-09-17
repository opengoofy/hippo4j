import { createFromIconfontCN } from '@ant-design/icons';

interface Props {
  type: string;
}

const MyIcon = createFromIconfontCN({
  scriptUrl: '//at.alicdn.com/t/c/font_4254722_3l4m6by7h34.js', // 在 iconfont.cn 上生成
});

const IconFont = (props: Props) => {
  return <MyIcon {...props}></MyIcon>;
};

export default IconFont;
