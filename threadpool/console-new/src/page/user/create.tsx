import { useRequest } from 'ahooks';
import { Form, Modal, Input, notification, Select, Checkbox, message } from 'antd';
import React, { useEffect } from 'react';
import { fetchAddUser, fetchUpdateUser } from './service';
import { fetchTenantOptions } from '../tenant/service';

interface IProps {
  type: string;
  data: any;
  visible: boolean;
  onClose: () => void;
  reset: () => void;
}

const ROLE_OPTIONS = [
  {
    label: 'ROLE_USER',
    value: 'ROLE_USER',
  },
  {
    label: 'ROLE_ADMIN',
    value: 'ROLE_ADMIN',
  },
  {
    label: 'ROLE_MANAGE',
    value: 'ROLE_MANAGE',
  },
];

const UserCreate: React.FC<IProps> = props => {
  const { visible, onClose, data, type, reset } = props;
  const [form] = Form.useForm();
  const tenantRequest = useRequest(fetchTenantOptions, {
    ready: !!type,
  });
  const updateRequest = useRequest(fetchUpdateUser, {
    manual: true,
    onSuccess: () => {
      notification.success({ message: '更新成功' });
      form.resetFields();
      onClose();
      reset();
    },
    onError: err => {
      message.error(err.message);
    },
  });
  const addRequest = useRequest(fetchAddUser, {
    manual: true,
    onSuccess: () => {
      notification.success({ message: '创建成功' });
      form.resetFields();
      onClose();
      reset();
    },
    onError: err => {
      message.error(err.message);
    },
  });
  const onSave = () => {
    form
      .validateFields()
      .then(values => {
        addRequest.run({ ...values });
      })
      .catch(info => {
        console.log('Validate Failed:', info);
      });
  };
  const onUpdate = () => {
    form
      .validateFields()
      .then(values => {
        updateRequest.run({ id: data.id, ...values });
      })
      .catch(info => {
        console.log('Validate Failed:', info);
      });
  };
  useEffect(() => {
    if (type === 'edit') {
      form.setFieldsValue({
        ...data,
      });
    }
  }, [type, data, form]);

  return (
    <Modal
      open={visible}
      onCancel={onClose}
      width={600}
      title={type === 'edit' ? '编辑' : '创建'}
      onOk={type === 'edit' ? onUpdate : onSave}
      confirmLoading={addRequest.loading || updateRequest.loading}
    >
      <Form labelAlign="right" labelCol={{ span: 6 }} wrapperCol={{ span: 15 }} form={form}>
        <Form.Item label="用户名" name="userName" rules={[{ required: true }]}>
          <Input placeholder="请输入" disabled={type === 'edit'} />
        </Form.Item>
        <Form.Item label="密码" name="password" rules={[{ required: true }]}>
          <Input placeholder="请输入" />
        </Form.Item>
        <Form.Item label="角色" name="role" rules={[{ required: true }]}>
          <Select options={ROLE_OPTIONS} placeholder="请选择" disabled={type === 'edit'} />
        </Form.Item>
        <Form.Item label="租户" name="tempResources">
          <Checkbox.Group options={tenantRequest.data} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default UserCreate;
