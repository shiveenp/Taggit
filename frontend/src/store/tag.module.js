import axios from "axios";
import {TAGGIT_BASE_API_URL} from "@/common/config";

const state = {
  allTags: [],
  activeTags: []
};

const getters = {
  allTags(state) {
    return state.allTags.sort();
  },
  activeTags(state) {
    return state.activeTags
  }
};

const mutations = {
  getAllTags(state, data) {
    state.allTags = data;
  },
  addTag(state, data) {
    let presentIndex = state.allTags.findIndex(tag => tag === data);
    if (presentIndex === -1) {
      state.allTags.push(data);
    }
  },
  removeTag(state, data) {
    state.allTags = state.allTags.filter(function (value) {
      return value !== data
    });
  },
  activateTag(state, data) {
    if (!state.activeTags.includes(data)) {
      state.activeTags.push(data)
    }
  },
  deactivateTag(state, data) {
    state.activeTags = state.activeTags.filter(function (value) {
      return value !== data
    })
  },
};

const actions = {
  fetchAllTags({ commit }, params) {
    commit('fetchingData');
    axios.get(TAGGIT_BASE_API_URL + '/user/' + params.userId + '/repos/tags', {
      headers: {
        'Content-Type': 'application/json',
        "x-taggit-session-key": localStorage.getItem("taggit-session-token")
      }
    })
        .then(({ data }) => {
          commit('getAllTags', data);
          commit('fetchFinished')
        })
        .catch(error => {
          commit('fetchFinished');
          throw new Error(error);
        });
  },
  addTag({commit}, data) {
    commit('addTag', data);
  },
  removeTag({commit}, data) {
    commit('removeTag', data);
  },
  activateTag({commit}, data) {
    commit('fetchingData');
    commit('activateTag', data);
    commit('fetchFinished')
  },
  deactivateTag({commit}, data) {
    commit('fetchingData');
    commit('deactivateTag', data);
    commit('fetchFinished')
  }
};

export default {
  state,
  getters,
  actions,
  mutations
};
