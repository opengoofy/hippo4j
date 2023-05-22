import {login} from '@/api/user';
import {getToken, removeToken, setToken} from '@/utils/auth';
import router, {resetRouter} from '@/router';
import {genKey, encrypt} from '@/utils/aes-util';

const state = {
    token: getToken(),
    name: '',
    avatar: '',
    introduction: '',
    roles: [],
};

const mutations = {
    SET_TOKEN: (state, token) => {
        state.token = token;
    },
    SET_INTRODUCTION: (state, introduction) => {
        state.introduction = introduction;
    },
    SET_NAME: (state, name) => {
        state.name = name;
    },
    SET_AVATAR: (state, avatar) => {
        state.avatar = avatar;
    },
    SET_ROLES: (state, roles) => {
        state.roles = roles;
    },
};

const actions = {
    // user login
    login({commit}, userInfo) {
        const {username, password} = userInfo;
        return new Promise((resolve, reject) => {
            let key = genKey();
            let encodePassword = encrypt(password, key)
            key = key.split("").reverse().join("")
            login({username: username.trim(), password: encodePassword, tag: key, rememberMe: 1})
                .then((response) => {
                    const {data} = response;
                    const {roles} = response;
                    commit('SET_TOKEN', data);
                    localStorage.setItem('roles', JSON.stringify(roles));
                    localStorage.setItem('USER_ROLE', roles[0]);
                    setToken(data);
                    resolve();
                })
                .catch((error) => {
                    // alert('登录失败');
                    reject(error);
                });
        });
    },


    // get user info
    getInfo({commit, state}) {
        return new Promise((resolve, reject) => {
            const data = {};
            data.roles = JSON.parse(localStorage.getItem('roles'));
            commit('SET_ROLES', data.roles);
            resolve(data);
        });
    },

    // user logout
    logout({commit, state}) {
        // return new Promise((resolve, reject) => {
        //   logout(state.token).then(() => {
        //     commit('SET_TOKEN', '')
        //     commit('SET_ROLES', [])
        //     removeToken()
        //     resetRouter()
        //     resolve()
        //   }).catch(error => {
        //     reject(error)
        //   })
        // })
        return new Promise((resolve) => {
            commit('SET_TOKEN', '');
            commit('SET_ROLES', []);
            removeToken();
            resetRouter();
            resolve();
        });
    },

    // remove token
    resetToken({commit}) {
        return new Promise((resolve) => {
            commit('SET_TOKEN', '');
            commit('SET_ROLES', []);
            removeToken();
            resolve();
        });
    },

    // dynamically modify permissions
    changeRoles({commit, dispatch}, role) {
        return new Promise(async (resolve) => {
            const token = role + '-token';

            commit('SET_TOKEN', token);
            setToken(token);

            const {roles} = await dispatch('getInfo');

            resetRouter();

            // generate accessible routes map based on roles
            const accessRoutes = await dispatch('permission/generateRoutes', roles, {root: true});

            // dynamically add accessible routes
            router.addRoutes(accessRoutes);

            // reset visited views and cached views
            dispatch('tagsView/delAllViews', null, {root: true});

            resolve();
        });
    },
};

export default {
    namespaced: true,
    state,
    mutations,
    actions,
};
