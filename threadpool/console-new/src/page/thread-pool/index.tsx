import { Form, Select, Space, Table, Typography } from 'antd';
import { useFormStateToUrl, useTran } from '@/hooks';
import { STR_MAP } from '@/config/i18n/locales/constants';
import { ColumnProps } from 'antd/es/table';
import { SearchButton, AddButton } from '@/components/with-button';
import { useAntdTable } from 'ahooks';
import service, { Result, ThreadPoolTableBody } from './service';
const { Title } = Typography;
const { Item } = Form;
const paramsType = { project: 0, thpool: 0 };

const ThreadPoll = () => {
  const [form] = Form.useForm();

  const { handleSetUrlState } = useFormStateToUrl<{ project: number; thpool: number }>(form, paramsType);
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
      dataIndex: 'allowCoreThreadTimeOut',
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

  const getTableData = (): Promise<Result> => {
    return service.fetchThreadPoolTable({
      current: 1,
      size: 10,
      tpId: '',
      itemId: '',
    });
  };

  const { tableProps, search } = useAntdTable<Result, ThreadPoolTableBody>(getTableData, {
    defaultPageSize: 5,
    form: form,
  });

  const { submit } = search;

  return (
    <Space direction="vertical" style={{ width: '100%' }} size="large">
      <Title>{useTran(STR_MAP.THREAD_POOL)}</Title>
      <Form form={form} layout="inline">
        <Item name="tenatIds" style={{ flex: 1 }}>
          <Select options={[{ label: '哈哈哈', value: 1 }]} placeholder={useTran(STR_MAP.PROJECT)}></Select>
        </Item>
        <Item name="project" style={{ flex: 1 }}>
          <Select options={[{ label: '哈哈哈', value: 1 }]} placeholder={useTran(STR_MAP.PROJECT)}></Select>
        </Item>
        <Item name="thpool" style={{ flex: 1 }}>
          <Select options={[{ label: '哈哈哈', value: 1 }]} placeholder={useTran(STR_MAP.PROJECT)}></Select>
        </Item>
        <Item style={{ flex: 4 }}>
          <Space>
            <SearchButton
              type="primary"
              htmlType="submit"
              onClick={() => {
                submit();
                handleSetUrlState();
              }}
            >
              {useTran(STR_MAP.SEARCH)}
            </SearchButton>
            <AddButton type="primary">{useTran(STR_MAP.ADD)}</AddButton>
          </Space>
        </Item>
      </Form>
      <Table columns={columns} {...tableProps}></Table>
    </Space>
  );
};
export default ThreadPoll;
