import { Button, Form, Select, Space, Switch, Table, Typography, message } from 'antd';
import { useFormStateToUrl, useTran } from '@/hooks';
import { STR_MAP } from '@/config/i18n/locales/constants';
import { ColumnProps } from 'antd/es/table';
import { SearchButton, AddButton } from '@/components/with-button';
import { useAntdTable } from 'ahooks';
import service from './service';
import { Result, Record } from './type';
import { QUEUE_TYPE_MAP, REJECT_TYPE_MAP, eBtnStyle, paramsType } from './constants';
import { useTranslation } from 'react-i18next';
import request from '@/utils';
const { Title } = Typography;
const { Item } = Form;

interface CustomTableParams {
  pageSize: number;
  current: number;
  tpId: string;
  itemId: string;
}

const ThreadPoll = () => {
  const [form] = Form.useForm();
  const { handleSetUrlState } = useFormStateToUrl<{ project: number; thpool: number }>(form, paramsType);
  const { t } = useTranslation();

  const columns: ColumnProps<Record>[] = [
    {
      title: useTran(STR_MAP.SERIAL_NUMBER),
      dataIndex: '',
      render: (col, num, index) => {
        return index + 1;
      },
      fixed: 'left',
      width: 80,
    },
    {
      title: useTran(STR_MAP.PROJECT),
      dataIndex: 'tenantId',
      width: 150,
    },
    {
      title: useTran(STR_MAP.THREAD_POOL),
      dataIndex: 'tpId',
      width: 150,
    },
    {
      title: useTran(STR_MAP.CORE_THREAD),
      dataIndex: 'coreSize',
      render: col => <span style={{ color: 'green' }}>{col}</span>,
      width: 100,
    },
    {
      title: useTran(STR_MAP.MAXIMUM_THREAD),
      dataIndex: 'maxSize',
      render: col => <span style={{ color: 'red' }}>{col}</span>,
      width: 100,
    },
    {
      title: useTran(STR_MAP.QUEUE_TYPE),
      dataIndex: 'queueType',
      render: (col: number) => QUEUE_TYPE_MAP[String(col)],
      width: 150,
    },
    {
      title: useTran(STR_MAP.QUEUE_CAPACITY),
      dataIndex: 'capacity',
      width: 150,
    },
    {
      title: useTran(STR_MAP.REJECTION_STRATEGY),
      dataIndex: 'rejectedType',
      render: (col: number) => REJECT_TYPE_MAP[String(col)] ?? 'CustomRejectedPolicy_' + col,
      width: 150,
    },
    {
      title: useTran(STR_MAP.EXECUTION_TIMEOUT),
      dataIndex: 'executeTimeOut',
      render: (col: number) => col ?? 0,
      width: 100,
    },
    {
      title: useTran(STR_MAP.ALARM_OR_NOT),
      dataIndex: 'isAlarm',
      render: (col: number, row) => (
        <Switch checked={Boolean(col)} onChange={() => handleAlarm({ id: row?.id, alarm: Number(!col) })} />
      ),
      width: 100,
    },
    {
      title: useTran(STR_MAP.CREATION_TIME),
      dataIndex: 'gmtCreate',
      width: 150,
      align: 'center',
    },
    {
      title: useTran(STR_MAP.UPDATE_TIME),
      dataIndex: 'gmtModified',
      width: 150,
      align: 'center',
    },
    {
      title: useTran(STR_MAP.EDIT),
      dataIndex: 'eidt',
      fixed: 'right',
      width: 150,
      render: () => (
        <>
          <Button type="link" style={eBtnStyle}>
            编辑
          </Button>
          <Button type="link" style={eBtnStyle}>
            删除
          </Button>
        </>
      ),
    },
  ];

  const getTableData = (params: CustomTableParams): Promise<Result> => {
    const { pageSize, current } = params;
    return service.fetchThreadPoolTable({
      current,
      size: pageSize,
      tpId: '',
      itemId: '',
    });
  };

  const { tableProps, search } = useAntdTable(getTableData, {
    defaultPageSize: 5,
    form: form,
  });

  const { submit } = search;

  const handleAlarm = async (params: { id: string; alarm: number }) => {
    const { id, alarm } = params;
    request(`/hippo4j/v1/cs/thread/pool/alarm/enable/${id}/${alarm}`, {
      method: 'post',
    })
      .then(res => {
        if (res.success) {
          message.success(t(STR_MAP.ALARM_EDITING_SUCCESS));
          submit();
        }
      })
      .catch(err => {
        console.log('err:::', err);
      });
  };

  return (
    <Space direction="vertical" size="large" style={{ display: 'flex' }}>
      <Title>{useTran(STR_MAP.THREAD_POOL_MANAGE)}</Title>
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
      <Table scroll={{ x: 1300 }} columns={columns} {...tableProps}></Table>
    </Space>
  );
};
export default ThreadPoll;
