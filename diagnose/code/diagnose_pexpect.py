#!/usr/bin/env python
#-*- coding: UTF-8 -*-
import pexpect
import sys
import argparse
import configparser
import os
import datetime
import re


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

def create_file():
    path = "diagnose_info"
    if not os.path.isdir(path):
        os.makedirs(path)
    files_list = os.listdir(path)
    dict = {}
    for i in files_list:
        all_path = os.path.join(path, i)
        ctime = os.path.getctime(all_path)
        dict[all_path] = ctime
    AllPathCtimeList = sorted(dict.items(), key=lambda item: item[1])
    if len(AllPathCtimeList) <= 10:
        pass
    else:
        for i in range(len(AllPathCtimeList) - 10):
            os.remove(AllPathCtimeList[i][0])
    finame = 'diagnose_info/'  + datetime.datetime.now().strftime('%Y%m%d_%H-%M-%S')+ '.txt'
    fout = file(finame, 'w')
    print finame
    return fout

def child_expect(child,expectStr):
    try:
        child.expect(expectStr,timeout=7)
    except Exception, e:
        child.sendline("\n###               Failure diagnosis               ###")
        child.close(force=True)
        exit(2)

def analysis_alarm_hot(echo_info):
    switched_tunnel = re.findall(r'[(](.*?)[)]', echo_info)
    if len(switched_tunnel) == 0:
        return 'There is no switched tunnel'
    else:
        ret = 'Switched info: \n  '
        tunnels = []
        for i in range(len(switched_tunnel)):
            tunnel = switched_tunnel[i]
            tuneldetail = tunnel.split(',')
            for j in range(len(tuneldetail)):
                if tuneldetail[j].find('TunnelName') > 0:
                    TunnelName = tuneldetail[j].split('=')
                    tunnels.append(TunnelName[1])
                    break
        return ret + ','.join(tunnels)
def analysis_alarm_bfd(echo_info):
    echo_info = 'Sep  9 2019 03:00:45.476+08:00 CNTZRT12001 %%01OSPF/3/NBR_DOWN_REASON(l):CID=0x808304ee;Neighbor state left full or changed to Down. (ProcessId=65100, NeighborRouterId=145.8.177.221, NeighborIp=144.1.241.102, NeighborAreaId=0.0.0.0, NeighborInterface=GigabitEthernet1/0/0, NeighborDownImmediate reason=Neighbor Down Due to Kill Neighbor, NeighborDownPrimeReason=BFD Session Down, CpuUsage=10%, VpnName=_public_, IfMTU=1500, LocalIp=144.1.241.101)'
    bfddownString = re.findall(r'[(](.*?)[)]', echo_info)
    ret = '\nBFD info:'
    if len(bfddownString) == 0:
        ret = ret + '\n  BFD没有问题'
    else:
        for i in range(len(bfddownString)):
            bfd = bfddownString[i]
            bfddetail = bfd.split(',')
            for j in range(len(bfddetail)):
                if bfddetail[j].find('NeighborIp') > 0:
                    NeighborIp = bfddetail[j].split('=')
                    ret = ret + '\n  链路: ' + NeighborIp[1] + '---'
                if bfddetail[j].find('NeighborIp') > 0:
                    NeighborIp = bfddetail[j].split('=')
                    ret = ret + '' + NeighborIp[1] + ', 发生过中断，可能是导致隧道down的原因'
    return ret
def analysis_ospf_up(echo_info):
    ret = '\nOSPF info:'
    areas = echo_info.split('Area')
    if len(areas) == 0:
        return '\n  There is no ospf info'
    for i in range(1,len(areas)):
        area = areas[i].strip()
        areadetail = area.split(' ')
        ret = ret + '\n  Area ' + areadetail[0]  + " interface " + ''.join(re.findall(r'[(](.*?)[)]', area))
        ret = ret + ' up time : ' + areadetail[-1]
        uptime = re.findall(r'\d+', areadetail[-1])
        if int(uptime[0]) >= 1 :
            ret = ret + " ，OSPF形成时间大于1小时，不是OSPF的问题"
        else:
            ret = ret + " ，OSPF形成时间小于1小时，OSPF可能有问题"
    return ret
def analysis_logbuffer_hot(echo_info):
    ret = '\nLogbuffer info:'
    ret = ret + '\n  There is no logbuffer info'
    return ''
def analysis_trap_down_event(echo_info):
    echo_info = 'SH_NE40E-X3-1 %%01L3VPN/2/L3VPN_MIB_TRAP_TUNNEL_UPDOWN_EVENT(t):CID=0x8014054c-OID=1.3.6.1.4.1.2011.5.25.177.8.1;The tunnel up/down event is occurred. (VpnIndex=6, NextHop=35.8.177.222, Ckey=16777706, TrapType=2)'
    ret = ''
    vpnDownList = re.findall(r'[(](.*?)[)]', echo_info)
    if len(vpnDownList) == 0:
        ret = ret + '\n  没有发现vpnDown'
    else:
        for i in range(len(vpnDownList)):
            bfd = vpnDownList[i]
    pass

def analysis_vpn_down(child):

    pass

def pexpect_execmd(hostname, deviceName, username, password, cmdfile):
    child = pexpect.spawn('ssh  -o StrictHostKeyChecking=no %s@%s' % (username,hostname))
    child_expect(child,'(?i)ssword:')
    child.sendline("%s" % password)
    child_expect(child,"(?i)N]:")
    child.sendline("n")
    child_expect(child,deviceName)
    child.sendline("###  The following is the diagnostic information  ###")
    child.logfile = create_file()
    child_expect(child,deviceName)
    finame = child.logfile.name
    f = open(cmdfile)
    line = f.readlines()
    cmd_index = 0
    analysis = ''
    if cmdfile.find('tunnel_down') != -1:
        analysis_vpn_down(child)
    else:
        for cmd in line:
            cmd = cmd.strip()
            child.sendline(cmd)
            child_expect(child,deviceName)
            if cmdfile.find('tunnel_down') != -1:
                if cmd_index == 0:
                    analysis = analysis + '\n\n\n' + '='*20 + '问题分析' + '='*20 + '\n'
                    analysis = analysis + analysis_alarm_hot(child.before)
                if cmd_index == 1:
                    analysis = analysis + analysis_alarm_bfd(child.before)
                if cmd_index == 2:
                    analysis = analysis + analysis_ospf_up(child.before)
                if cmd_index == 3:
                    analysis = analysis + analysis_logbuffer_hot(child.before)
            cmd_index = cmd_index + 1
    f.close()
    child.close(force=True)
    with open(finame, 'a+') as analysis_f:
        analysis_f.write(analysis)
        analysis_f.close()

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


