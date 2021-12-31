import Vue from 'vue'
import Router from 'vue-router'
import Home from "../views/Home";
import Account from "../views/Account";

Vue.use(Router);

const router = new Router({
  mode: 'history',
  routes: [
    {path: '/', name: 'home', component: Home},
    {path: '/account', name: 'account', component: Account},

    // otherwise redirect to home
    {path: '*', redirect: '/'}
  ]
})

export default router;
