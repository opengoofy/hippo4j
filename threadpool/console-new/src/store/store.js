import { createStore} from 'redux';
import reducer from './reducer'

const initState = { money: 0 };

const store = createStore(
    reducer,
    initState,
);

export default store;
