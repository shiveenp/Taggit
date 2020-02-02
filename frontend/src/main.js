import Vue from 'vue'
import App from './App.vue'
import { router } from './helpers/router'
import Buefy from 'buefy'
import VueAxios from "vue-axios";
import axios from 'axios'

Vue.use(Buefy);
Vue.use(VueAxios, axios);

Vue.config.productionTip = false;

new Vue({
  render: h => h(App),
  router

}).$mount('#app');
