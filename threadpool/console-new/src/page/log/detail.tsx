import { Descriptions, Modal } from 'antd';
import React from 'react';

const LogDetail: React.FC<{
  data: any;
  visible: boolean;
  onClose: () => void;
}> = props => {
  const { visible, onClose, data } = props;
  return (
    <Modal open={visible} onCancel={onClose} footer={null} width={600}>
      <Descriptions title={'详情'} column={1}>
        <Descriptions.Item span={1} label="业务类型">
          {data.category}
        </Descriptions.Item>
        <Descriptions.Item span={1} label="业务标识">
          {data.bizNo}
        </Descriptions.Item>
        <Descriptions.Item span={1} label="操作人">
          {data.operator}
        </Descriptions.Item>
        <Descriptions.Item span={1} label="创建时间">
          {data.createTime}
        </Descriptions.Item>
        <Descriptions.Item span={1} label="日志内容">
          {data.action}
        </Descriptions.Item>
      </Descriptions>
    </Modal>
  );
};

export default LogDetail;
