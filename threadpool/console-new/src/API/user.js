import request from '../axios/request';

export function userLogin(data) {
  return request('post', '/hippo4j/v1/cs/auth/login', data);
}

export function getInfo() {
  return request('get', '/hippo4j/v1/cs/user/info');
}

export function logout() {
  return request('post', '/hippo4j/v1/cs/user/logout');
}

function islogin({ commit }, userInfo) {
  const { username, password } = userInfo;
  return new Promise((resolve, reject) => {
    let key = genKey();
    let encodePassword = encrypt(password, key);
    key = key.split('').reverse().join('');
    login({ username: username.trim(), password: encodePassword, tag: key, rememberMe: 1 })
      .then(response => {
        const { data } = response;
        const { roles } = response;
        commit('SET_TOKEN', data);
        localStorage.setItem('roles', JSON.stringify(roles));
        localStorage.setItem('USER_ROLE', roles[0]);
        setToken(data);
        resolve();
      })
      .catch(error => {
        // alert('登录失败')
        reject(error);
      });
  });
}

// get user info
function getInfo({ commit, state }) {
  return new Promise((resolve, reject) => {
    const data = {};
    data.roles = JSON.parse(localStorage.getItem('roles'));
    commit('SET_ROLES', data.roles);
    resolve(data);
  });
}

// user logout
function logout({ commit, state }) {
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
  return new Promise(resolve => {
    commit('SET_TOKEN', '');
    commit('SET_ROLES', []);
    removeToken();
    resetRouter();
    resolve();
  });
}

// remove token
function resetToken({ commit }) {
  return new Promise(resolve => {
    commit('SET_TOKEN', '');
    commit('SET_ROLES', []);
    removeToken();
    resolve();
  });
}

// dynamically modify permissions
function changeRoles({ commit, dispatch }, role) {
  return new Promise(async resolve => {
    const token = role + '-token';

    commit('SET_TOKEN', token);
    setToken(token);

    const { roles } = await dispatch('getInfo');

    resetRouter();

    // generate accessible routes map based on roles
    const accessRoutes = await dispatch('permission/generateRoutes', roles, { root: true });

    // dynamically add accessible routes
    router.addRoutes(accessRoutes);

    // reset visited views and cached views
    dispatch('tagsView/delAllViews', null, { root: true });

    resolve();
  });
}

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

export default {
  userLogin,
  getInfo,
  logout,
};
