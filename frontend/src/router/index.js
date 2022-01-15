import Vue from 'vue'
import Router from 'vue-router'
import Home from "../views/Home";
import LoginPage from "@/views/LoginPage";

Vue.use(Router);

const router = new Router({
  mode: 'history',
  routes: [
    {path: '/', name: 'home', component: Home},
    {path: '/login', name: 'login', component: LoginPage},

    // otherwise redirect to home
    {path: '*', redirect: '/'}
  ]
});

router.beforeEach((to, from, next) => {
  // redirect to login page if not logged in and trying to access a restricted page
  const isAuthenticated = localStorage.getItem("taggit:loggedIn");
  const authRequired = to.name !== 'login';

  if (authRequired && !isAuthenticated) {
    next({name: 'login'});
  } else {
    next();
  }
});

export default router;
