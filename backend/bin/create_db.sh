#!/bin/bash
sudo -u shiveenp psql -c "create database gitstars;"
sudo -u shiveenp psql -c "create user gitstars_admin with encrypted password 'localpassword';"
sudo -u shiveenp psql -c "grant all privileges on database gitstars to gitstars_admin;"
sudo -u shiveenp psql -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
