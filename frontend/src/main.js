import Vue from 'vue'
import App from './App.vue'
import router from "./router";
import store from "./store";
import Buefy from 'buefy'
import VueLazyload from 'vue-lazyload';
import Paginate from 'vuejs-paginate';

Vue.use(Buefy, {
  defaultIconPack: 'fas'
});
Vue.use(VueLazyload);
Vue.component('paginate', Paginate);

Vue.config.productionTip = false;

new Vue({
  render: h => h(App),
  router,
  store

}).$mount('#app');
