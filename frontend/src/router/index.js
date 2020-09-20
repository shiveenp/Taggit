import Vue from 'vue'
import Router from 'vue-router'
import Login from '../views/Login'
import Home from "../views/Home";
import Account from "../views/Account";
import TokenCapture from '@/views/TokenCapture';

Vue.use(Router);

const router = new Router({
  mode: 'history',
  routes: [
    {path: '/', name: 'login', component: Login},
    {path: "/user/:userId/token", name: 'tokenCapture', component: TokenCapture},
    {path: '/user/:userId', name: 'home', component: Home},
    {path: '/user/:userId/account', name: 'account', component: Account},

    // otherwise redirect to home
    {path: '*', redirect: '/'}
  ]
})


router.beforeEach((to, from, next) => {
  // redirect to login page if not logged in and trying to access a restricted page
  const isAuthenticated = localStorage.getItem("taggit-session-token")
  const authRequired = to.name !== 'login' && to.name !== 'tokenCapture';

  if (authRequired && !isAuthenticated) {
    next({name: 'login'});
  } else {
    next();
  }
})

export default router;