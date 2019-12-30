import Vue from 'vue'
import App from './App.vue'
import { router } from './helpers/router'
import Buefy from 'buefy'

Vue.use(Buefy)

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  router

}).$mount('#app')
