import { Button, Form, Select, Space, Table, Typography } from 'antd';
import { useTran } from '@/hooks';
import { STR_MAP } from '@/config/i18n/locales/constants';
import { ColumnProps } from 'antd/es/table';
const { Title } = Typography;
const { Item } = Form;

const ThreadPoll = () => {
  const [form] = Form.useForm();
  const columns: ColumnProps<any>[] = [
    {
      title: useTran(STR_MAP.SERIAL_NUMBER),
      dataIndex: 'order',
    },
    {
      title: useTran(STR_MAP.SERIAL_NUMBER),
      dataIndex: 'tenantry',
    },
    {
      title: useTran(STR_MAP.SERIAL_NUMBER),
      dataIndex: 'tenantry',
    },
  ];
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
            <Button type="primary">{useTran(STR_MAP.SEARCH)}</Button>
            <Button type="primary">{useTran(STR_MAP.ADD)}</Button>
          </Space>
        </Item>
      </Form>
      <Table columns={columns}></Table>
    </Space>
  );
};
export default ThreadPoll;
