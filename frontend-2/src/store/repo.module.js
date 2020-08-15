import axios from "axios";
import qs from "qs";
import {TAGGIT_BASE_API_URL, TAGGIT_BASE_WS_URI} from "../common/config";

const state = {
  isSyncing: false,
  syncProgressPercent: 0,
  syncStatus: '',
  repos: [],
  reposToDisplay: [],
  pageNm: '1',
  pageSize: '51',
  total: ''
};

const getters = {
  isSyncing(state) {
    return state.isSyncing;
  },
  syncProgressPercent(state) {
    return state.syncProgressPercent;
  },
  syncStatus(state) {
    return state.syncStatus;
  },
  repos(state) {
    return state.repos;
  },
  reposToDisplay(state) {
    return state.reposToDisplay;
  },
  total(state) {
    return state.total;
  },
  pageNm(state) {
    return state.pageNm;
  },
  pageSize(state) {
    return state.pageSize;
  }
};

const mutations = {
  changeIsSyncing(state, data) {
    state.isSyncing = data;
  },
  getSyncProgress(state, data) {
    state.syncProgressPercent = data.progressPercent * 100;
    state.syncStatus = data.status;
  },
  getRepoData(state, data) {
    state.repos = data.data;
    state.reposToDisplay = data.data;
    state.total = data.total;
  },
  getActiveTagRepoData(state, data) {
    state.reposToDisplay = data
  },
  changePageNm(state, data) {
    state.pageNm = data;
  }
};

const actions = {
  resyncRepos({commit}, params) {
    commit("changeIsSyncing", true)
    commit("getSyncProgress", {progressPercent: 0, status: 'SyncStarted'});
    axios.post(TAGGIT_BASE_API_URL + "/user/" + params.userId + "/sync").then(response => {
      let syncJobId = response.data;
      params.vmInstance.$connect(TAGGIT_BASE_WS_URI + '/' + syncJobId, {format: 'json'});
      params.vmInstance.$options.sockets.onmessage = (data) => {
        let wsResponse = JSON.parse(data.data);
        commit("getSyncProgress", wsResponse);
        if (state.syncStatus === 'Update completed!') {
          params.vmInstance.$buefy.toast.open('Repo sync completed, please refresh page 🚀');
          commit("changeIsSyncing", false);
        }
      };
    }).catch(function (error) {
      console.log(error);
      commit("changeIsSyncing", false);
      commit("getSyncProgress", {progressPercent: 0, status: ''});
    });
  }
  ,
  fetchRepos({commit}, params) {
    commit('fetchingData');
    axios.get(TAGGIT_BASE_API_URL + '/user/' + params.userId + '/repos' + '?pageNm=' + state.pageNm + '&pageSize=' + state.pageSize, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
        .then(({data}) => {
          commit('getRepoData', data);
          commit('fetchFinished')
        })
        .catch(error => {
          commit('fetchFinished');
          throw new Error(error);
        });
  },
  fetchReposUsingTags({commit}, params) {
    commit('fetchingData');
    axios.get(TAGGIT_BASE_API_URL + '/user/' + params.userId + '/repo/search', {
      params: {
        tag: params.tags
      },
      paramsSerializer: function (params) {
        return qs.stringify(params, {arrayFormat: 'repeat'})
      },
      headers: {
        'Content-Type': 'application/json'
      }
    }).then(({data}) => {
      commit('getActiveTagRepoData', data);
      commit('fetchFinished')
    })
        .catch(error => {
          commit('fetchFinished');
          throw new Error(error);
        });
  },
  changePageNm({commit}, data) {
    commit('changePageNm', data);
  }
};

export default {
  state,
  getters,
  actions,
  mutations
};
