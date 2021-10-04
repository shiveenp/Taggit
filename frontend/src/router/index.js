import Vue from 'vue'
import Router from 'vue-router'
import Login from '../views/Login'
import Home from "../views/Home";
import Account from "../views/Account";
import LandingPage from "@/views/LandingPage";
import TokenCapture from '@/views/TokenCapture';

Vue.use(Router);

const router = new Router({
  mode: 'history',
  routes: [
    {path: '/', name: 'landingPage', component: LandingPage},
    {path: "/login", name: 'login', component: Login},
    {path: "/user/:userId/token", name: 'tokenCapture', component: TokenCapture},
    {path: '/home', name: 'home', component: Home},
    {path: '/account', name: 'account', component: Account},

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
