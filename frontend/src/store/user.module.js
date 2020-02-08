import axios from "axios";

const state = {
  id: '',
  userName: '',
  email: '',
  githubUserName: '',
  githubUserId: '',
  isLoading: true,
};

const getters = {
  userName(state) {
    return state.userName
  },
  email(state) {
    return state.email
  },
  githubUserName(state) {
    return state.githubUserName
  },
  githubUserId(state) {
    return state.githubUserId
  },
  isLoading(state) {
    return state.isLoading
  }
};

const mutations = {
  fetchingData(state) {
    state.isLoading = true
  },
  getUserData(state, data) {
    state.id = data.id;
    state.userName = data.userName;
    state.email = data.email;
    state.githubUserName = data.githubUserName;
    state.githubUserId = data.githubUserId;
    state.isLoading = false;
  }
};

const actions = {
  fetchUser({ commit }, params) {
    commit('fetchingData');
    axios.get('http://localhost:9001/user/' + params.userId, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
        .then(({ data }) => {
          commit('getUserData', data);
        })
        .catch(error => {
          throw new Error(error);
        });
  }
};

export default {
  state,
  getters,
  actions,
  mutations
};
