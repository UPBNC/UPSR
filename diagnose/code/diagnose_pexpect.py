#!/usr/bin/env python
#-*- coding: UTF-8 -*-
import pexpect
import sys
import argparse
import configparser

def arg_parse():
    parser = argparse.ArgumentParser(description='Diagnose')
    parser.add_argument("--routerId", dest='routerId', help="routerId",default="1.1.1.1", type=str)
    parser.add_argument("--deviceName", dest='deviceName', help="deviceName",default="<SHPE1>", type=str)
    parser.add_argument("--host", dest='host', help="SSH IP",default="127.0.0.1", type=str)
    parser.add_argument("--port", dest='port', help="SSH port",default="22", type=str)
    parser.add_argument("--username", dest='username', help="username",default="root", type=str)
    parser.add_argument("--password", dest='password', help="password",default="123456", type=str)
    parser.add_argument("--cmdfile", dest='cmdfile', help="CMD file name",default="/root/tools/test/o2/karaf-0.8.2/diagnose/cmd/vpn_down.txt", type=str)
    return parser.parse_args()

def pexpect_execmd(hostname, deviceName, username, password, cmdfile):
    child = pexpect.spawn('ssh  -o StrictHostKeyChecking=no %s@%s' % (username,hostname))
    child.expect('(?i)ssword:')
    child.sendline("%s" % password)
    child.expect("(?i)N]:")
    child.sendline("n")
    child.expect(deviceName)
    child.logfile = sys.stdout
    f = open(cmdfile)
    line = f.readlines()
    for cmd in line:
        cmd = cmd.strip()
        child.sendline(cmd)
        child.expect(deviceName)
    f.close()
    child.close()

if __name__ == '__main__':
    args = arg_parse()
    routerId = args.routerId
    host = args.host
    port = args.port
    username = args.username
    password = args.password
    cmdfile = args.cmdfile
    deviceName = args.deviceName
    deviceCfg = configparser.ConfigParser()
    deviceCfg.read('/root/upmdc/o2/karaf-0.8.2/sr_conf.ini')
    for i, val in enumerate(deviceCfg.sections()):
        if deviceCfg.get((val), "routerId") == routerId:
            host = deviceCfg.get((val), "sshIP")
            username = deviceCfg.get((val), "userName")
            password = deviceCfg.get((val), "passWord")
            pexpect_execmd(host, deviceName, username, password, cmdfile)
            break


