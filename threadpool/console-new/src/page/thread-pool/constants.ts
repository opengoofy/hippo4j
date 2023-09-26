export const QUEUE_TYPE_MAP: { [key: string]: string } = {
  '1': 'ArrayBlockingQueue',
  '2': 'LinkedBlockingQueue',
  '3': 'LinkedBlockingDeque',
  '4': 'SynchronousQueue',
  '5': 'LinkedTransferQueue',
  '6': 'PriorityBlockingQueue',
  '9': 'ResizableLinkedBlockingQueue',
};

export const REJECT_TYPE_MAP: { [key: string]: string } = {
  '1': 'CallerRunsPolicy',
  '2': 'AbortPolicy',
  '3': 'DiscardPolicy',
  '4': 'DiscardOldestPolicy',
  '5': 'RunsOldestTaskPolicy',
  '6': 'SyncPutQueuePolicy',
};

export const paramsType = { project: 0, thpool: 0 };
export const eBtnStyle = {
  padding: '0 6px',
};
