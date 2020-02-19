import _ from 'lodash';

const state = {
  activeTags: []
};

const getters = {
  activeTags(state) {
    return state.activeTags
  }
};

const mutations = {
  activateTag(state, data) {
    console.log(state.activeTags);
    if (!state.activeTags.includes(data)) {
      console.log(`state does not include ${data}`)
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
  activateTag({commit}, data) {
    console.log(`data is ${data}`)
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
