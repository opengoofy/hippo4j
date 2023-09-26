import { createFromIconfontCN } from '@ant-design/icons';
import React from 'react';
import style from './index.module.less';

const MyIcon = createFromIconfontCN({
  scriptUrl: '//at.alicdn.com/t/c/font_4254722_vw34zn7su2.js', // 在 iconfont.cn 上生成
});

type MyComponentProps = React.HTMLProps<HTMLDivElement> & { type: string };

const IconFont: React.FC<MyComponentProps> = props => {
  return <MyIcon className={style['custom-icon']} {...props}></MyIcon>;
};

export default IconFont;
