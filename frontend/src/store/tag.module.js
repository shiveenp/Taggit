import _ from 'lodash';

const state = {
  activeTags: []
};

const getters = {
  activeTag(state) {
    return state.activeTags
  }
};

const mutations = {
  activateTag(state, data) {
    state.activeTags.push(data)
  },
  deactivateTag(state, data) {
    _.remove(state.activeTags, function (n) {
      return n === data;
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
