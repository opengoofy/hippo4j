import { useRequest } from 'ahooks';
import { Form, Modal, Input, Select } from 'antd';
import React from 'react';
import { fetchTenantList } from '../tenant/service';

const ItemCreate: React.FC<{
  type: string;
  data: any;
  visible: boolean;
  onClose: () => void;
}> = props => {
  const { visible, onClose, data, type } = props;
  const { data: tenant } = useRequest(fetchTenantList);
  console.log('tenant', tenant);

  return (
    <Modal open={visible} onCancel={onClose} footer={null} width={600} title={type === 'edit' ? '编辑' : '创建'}>
      <Form>
        <Form.Item label="租户" name="">
          {/* <Select options={tenant}></Select> */}
        </Form.Item>
        <Form.Item label="项目" name="">
          <Input />
        </Form.Item>
        <Form.Item label="负责人" name="">
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default ItemCreate;
