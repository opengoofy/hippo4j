import { Button, Form, Select, Space, Table, Typography } from 'antd';
import { useFormStateToUrl, useTran } from '@/hooks';
import { STR_MAP } from '@/config/i18n/locales/constants';
import { ColumnProps } from 'antd/es/table';
const { Title } = Typography;
const { Item } = Form;

const params = { project: 0, thpool: 0 };
const ThreadPoll = () => {
  const [form] = Form.useForm();
  const { handleSetUrlState } = useFormStateToUrl<{ project: number; thpool: number }>(form, params);
  const columns: ColumnProps<any>[] = [
    {
      title: useTran(STR_MAP.SERIAL_NUMBER),
      dataIndex: 'order',
    },
    {
      title: useTran(STR_MAP.PROJECT),
      dataIndex: 'project',
    },
    {
      title: useTran(STR_MAP.TENANTRY),
      dataIndex: 'tenantry',
    },
    {
      title: useTran(STR_MAP.THREAD_POOL),
      dataIndex: 'thread_pool',
    },
    {
      title: useTran(STR_MAP.MAXIMUM_THREAD),
      dataIndex: 'maximum_thread',
    },
    {
      title: useTran(STR_MAP.QUEUE_TYPE),
      dataIndex: 'queue_type',
    },
    {
      title: useTran(STR_MAP.REJECTION_STRATEGY),
      dataIndex: 'tenantry',
    },
    {
      title: useTran(STR_MAP.EXECUTION_TIMEOUT),
      dataIndex: 'tenantry',
    },
    {
      title: useTran(STR_MAP.ALARM_OR_NOT),
      dataIndex: 'tenantry',
    },
    {
      title: useTran(STR_MAP.CREATION_TIME),
      dataIndex: 'tenantry',
    },
    {
      title: useTran(STR_MAP.UPDATE_TIME),
      dataIndex: 'tenantry',
    },
    {
      title: useTran(STR_MAP.EDIT),
      dataIndex: 'tenantry',
    },
  ];

  const handleSubmit = () => {
    handleSetUrlState();
  };

  return (
    <Space direction="vertical" style={{ width: '100%' }} size="large">
      <Title>{useTran(STR_MAP.THREAD_POOL)}</Title>
      <Form form={form} layout="inline">
        <Item name="project" style={{ flex: 1 }}>
          <Select options={[{ label: '哈哈哈', value: 1 }]} placeholder={useTran(STR_MAP.PROJECT)}></Select>
        </Item>
        <Item name="thpool" style={{ flex: 1 }}>
          <Select options={[{ label: '哈哈哈', value: 1 }]} placeholder={useTran(STR_MAP.PROJECT)}></Select>
        </Item>
        <Item style={{ flex: 4 }}>
          <Space>
            <Button type="primary" htmlType="submit" onClick={handleSubmit}>
              {useTran(STR_MAP.SEARCH)}
            </Button>
            <Button type="primary">{useTran(STR_MAP.ADD)}</Button>
          </Space>
        </Item>
      </Form>
      <Table columns={columns}></Table>
    </Space>
  );
};
export default ThreadPoll;
