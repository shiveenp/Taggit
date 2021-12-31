import axios from "axios";

const state = {
    allTags: ['Untagged'],
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
    setAllTags(state, data) {
        if (Array.isArray(data) && data !== undefined && data.length) {
            state.allTags = state.allTags.concat(data);
        }
    },
    addTag(state, data) {
        let presentIndex = state.allTags.findIndex(tag => tag === data);
        if (presentIndex === -1) {
            state.allTags.push(data);
        }
    },
    removeTag(state, data) {
        state.allTags = state.allTags.filter(value => value !== data);
    },
    activateTag(state, data) {
        if (!state.activeTags.includes(data)) {
            state.activeTags.push(data)
        }
    },
    deactivateTag(state, data) {
        state.activeTags = state.activeTags.filter(value => value !== data);
    },
};

const actions = {
    fetchAllTags({commit}, params) {
        commit('fetchingData');
        axios.get('/api/repos/tags')
            .then(({data}) => {
                commit('setAllTags', data);
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
