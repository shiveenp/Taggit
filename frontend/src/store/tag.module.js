const state = {
  activeTag: ''
};

const getters = {
  activeTag(state) {
    return state.activeTag
  }
};

const mutations = {
  activateTag(state, data) {
    state.activeTag = data
  }
};

const actions = {
  activateTag({commit}, data) {
    commit('fetchingData');
    commit('activateTag', data)
    commit('fetchFinished')
  }
};

export default {
  state,
  getters,
  actions,
  mutations
};
