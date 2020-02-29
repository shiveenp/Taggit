import axios from "axios";

const state = {
  repos: []
};

const getters = {
  repos(state) {
    return state.repos
  }
};

const mutations = {
  getRepoData(state, data) {
    state.repos = data;
  }
};

const actions = {
  fetchRepos({ commit }, params) {
    commit('fetchingData');
    axios.get('http://localhost:9001/user/' + params.userId + '/repos', {
      headers: {
        'Content-Type': 'application/json'
      }
    })
        .then(({ data }) => {
          commit('getRepoData', data);
          commit('fetchFinished')
        })
        .catch(error => {
          commit('fetchFinished');
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
