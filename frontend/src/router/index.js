import Vue from 'vue'
import Router from 'vue-router'
import Home from "../views/Home";

Vue.use(Router);

const router = new Router({
  mode: 'history',
  routes: [
    {path: '/', name: 'home', component: Home},

    // otherwise redirect to home
    {path: '*', redirect: '/'}
  ]
})

export default router;
