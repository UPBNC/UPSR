#!/usr/bin/env python
#-*- coding: UTF-8 -*-
import pexpect
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
def analysis_business(tunnelName):
    dic_ww = {'1': '内网接入区', '2': '直连无卡',  '3':'第二非金','4':'直连机构',
           '5':'非金机构',  '6':'分公司接入区','7':'人行接入区','15':'测试PIT'}
    dic_xy = {'1':'上海CNTZRT12001','3':'上海CNTZRT12002',
              '2':'北京CNBJRT12001','4':'北京CNBJRT12002'}
    dic_z = {'5':'EF','4':'AF4','3':'AF3','1':'AF1'}
    dic_line = {'12':'移动线路','32':'移动线路',
              '14':'联通线路','34':'联通线路'}
    
    ret = ''
    tunnelId = filter(str.isdigit, tunnelName)
    if len(tunnelId) < 4 :
        return ret
    else:
        ret = ': ' + dic_ww[tunnelId[0:(len(tunnelId)-3)]] + ' 从 ' + dic_xy[tunnelId[len(tunnelId)-3]] +\
              ' 到 ' + dic_xy[tunnelId[len(tunnelId)-2]] + ' 的 ' + dic_z[tunnelId[len(tunnelId)-1]] + ' 业务,' +\
              '线路为: ' + dic_line[tunnelId[(len(tunnelId)-3):(len(tunnelId)-1)]]
    return ret
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
                    tunnels.append(TunnelName[1] + analysis_business(TunnelName[1]))
                    break
        tunnels.append('Tunnel7125'+analysis_business('Tunnel7125'))
        return ret + '\n  '.join(tunnels)
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
def get_vpn_down_index_list(child):
    echo_info = 'occurred. (VpnIndex=41, NextHop=35.8.177.222, Ckey=16777706, TrapType=2)'+\
                'occurred. (VpnIndex=58, NextHop=35.8.177.222, Ckey=16777706, TrapType=2)'
    ret = []
    vpnDownList = re.findall(r'[(](.*?)[)]', echo_info)
    if len(vpnDownList) == 0:
        return ret
    else:
        for i in range(len(vpnDownList)):
            vpnDownString = vpnDownList[i]
            if vpnDownString.find('TrapType=2') != -1:
                indexString = re.findall('VpnIndex=\d+',vpnDownString)
                index = filter(str.isdigit,  indexString[0])
                ret.append(index)
    return ret
def get_all_vpn_down_list(child,deviceName):
    ret = []
    child.sendline('display ip vpn-instance verbose | no-more | include "(VPN-Instance Name and ID :)|(Vrf Status :)"')
    child_expect(child, deviceName)
    echo_string = child.before
    allVpn = echo_string.strip().split('\r\n\r\n')
    if len(allVpn) >1:
        vpndetail = allVpn[1].strip().split('VPN-Instance Name and ID :')
        for i in range(1,len(vpndetail)):
            if vpndetail[i].strip().find('Vrf Status : DOWN') != -1:
                id = re.findall(', (\d+)', vpndetail[i].strip())
                ret.append(id[0])
    return ret
def get_cpn_name_by_index(child,index_list,deviceName):
    ret = []
    for i in range(len(index_list)):
        child.sendline('display ip vpn-instance verbose | no-more | include "VPN-Instance Name and ID"')
        child_expect(child, deviceName)
        echo_string = child.before
        nameIDList = echo_string.split('VPN-Instance Name and ID :')
        for j in range(1,len(nameIDList)):
            name = nameIDList[j].split(',')[0].strip()
            id = nameIDList[j].split(',')[1].strip()
            if id==index_list[i]:
                ret.append(name)
    return ret
def get_tunnel_policy_by_vpnname(child,deviceName,vpnName):
    child.sendline('display ip vpn-instance verbose ' + vpnName + ' | no-more | include "Tunnel Policy :"')
    child_expect(child, deviceName)
    echo_info = child.before
    policy = echo_info.split('Tunnel Policy :')
    if len(policy) > 1:
        return policy[1].strip()
    else:
        return ''
#vpn的第四步
def get_reserved_bind_alarm(child, deviceName,tunnelName):
    child.sendline('interface ' + tunnelName)
    child_expect(child, deviceName)
    child.sendline('mpls te reserved-for-binding')
    child_expect(child, deviceName)
    child.sendline('quit')
    child_expect(child, deviceName)
    child.sendline('display trapbuffer | no-more | include "The tunnel up event is occurred"')
    child_expect(child, deviceName)
    return True
#vpn的第五步
def get_te_interface_alarm(child, deviceName,tunnelNam):
    child.sendline('display trapbuffer | no-more | include "The tunnel up event is occurred"')
    child_expect(child, deviceName)
    return True
#vpn的第六步
def get_ldp_lsp_alarm(child,deviceName,vpnName):
    child.sendline('display trapbuffer | no-more | include "The tunnel up event is occurred"')
    child_expect(child, deviceName)
    return True
#vpn的第七步
def get_vpn_instance_status(child,deviceName,vpnName):
    child.sendline('display trapbuffer | no-more | include "The tunnel up event is occurred"')
    child_expect(child, deviceName)
    return True
def get_tunnel_list_by_tunnel_policy(child,deviceName,policyName):
    child.sendline('display tunnel-policy ' + policyName + ' | no-more | include Tunnel')
    child_expect(child, deviceName)
    echo_info = child.before
    tunnelList = re.findall('Tunnel\d+',echo_info)
    return tunnelList
def check_tunnel_up(child,deviceName,tunnelName):
    child.sendline('display interface ' + tunnelName + ' | no-more | include "Line protocol current state :"')
    child_expect(child, deviceName)
    echo_info = child.before
    if echo_info.strip().split(':')[-1] == 'UP':
        return True
    else:
        return False
def check_resvered_bind(child, deviceName, tunnelName):
    child.sendline('interface ' + tunnelName)
    child_expect(child, deviceName)
    child.sendline('display this | no-more')
    child_expect(child, deviceName)
    echo_tunnel_info = child.before
    if echo_tunnel_info.find('mpls te reserved-for-binding') == -1:
        child.sendline('quit ')
        child_expect(child, deviceName)
        return False
    else:
        child.sendline('quit ')
        child_expect(child, deviceName)
        return True
def collect_alarm_and_contact_hw(child,deviceName):
    child.sendline('display version')
    child_expect(child, deviceName)
    child.sendline('display esn')
    child_expect(child, deviceName)
    child.sendline('save logfile')
    child_expect(child, deviceName)
    child.sendline('system')
    child_expect(child, deviceName)
    child.sendline('diagnose')
    child_expect(child, deviceName)
    child.sendline('diaplay diagnostic-information >> diagnose.log')
    child_expect(child, deviceName)
    child.sendline('save logfile diagnose-log')
    child_expect(child, deviceName)
    child.sendline('quit')
    child_expect(child, deviceName)
    child.sendline('quit')
    child_expect(child, deviceName)
def diagnose_vpn_by_name(child,deviceName,vpnName):
    ret = ''
    policyName = get_tunnel_policy_by_vpnname(child, deviceName, vpnName)
    #1.使用命令display ip vpn-instance verbose xxx 检查tnl-policy字段是否存在。
    if policyName != '':
        #2.使用display tunnel-policy tunnel-policy-name检查隧道策略的内容。
        tunnelList = get_tunnel_list_by_tunnel_policy(child, deviceName, policyName)
        for i in range(len(tunnelList)):
            if check_tunnel_up(child, deviceName, tunnelList[i]) == True:
                # 3.进入Tunnel接口视图，执行display this命令检查Tunnel接口下是否配置了mpls te reserved-for-binding命令。
                if check_resvered_bind(child, deviceName,tunnelList[i]) == True:
                    #7.检查VPN实例的接口状态。查看status是否为UP，查看是否存在对端VPN路由。
                    if get_vpn_instance_status(child,deviceName,vpnName) == True:
                        ret = ret + ('\n  %-13s'%tunnelList[i]) + ' : hwTnl2VpnTrapEvent: The tunnel up event is occurred'
                    else:
                        ret = ret + ('\n  %-13s'%tunnelList[i]) + collect_alarm_and_contact_hw(child,deviceName)
                else:
                    #4.进入Tunnel接口视图，配置mpls te reserved-for-binding命令，然后看是否出现告警
                    if get_reserved_bind_alarm(child, deviceName,tunnelList[i]) == True:
                        ret = ret + ('\n  %-13s'%tunnelList[i]) + ' : hwTnl2VpnTrapEvent: The tunnel up event is occurred'
                    else:
                        ret = ret + ('\n  %-13s'%tunnelList[i]) + collect_alarm_and_contact_hw(child,deviceName)
            else:
                #5.检查TE接口下的配置，并根据TE相关的告警确认和排除问题，然后看是否出现告警
                if get_te_interface_alarm(child, deviceName, tunnelList[i]) == True:
                    ret = ret + ('\n  %-13s'%tunnelList[i]) + ' : hwTnl2VpnTrapEvent: The tunnel up event is occurred'
                else:
                    ret = ret + ('\n  %-13s'%tunnelList[i]) + collect_alarm_and_contact_hw(child,deviceName)
    else:
        #6.检查LDP LSP的配置，并根据LSP相关的告警确认和排除问题，然后看是否出现告警
        if get_ldp_lsp_alarm(child, deviceName, vpnName) == True:
            ret = ret + '\n  ' + 'The tunnel up event is occurred'
        else:
            ret = ret + '\n  ' + collect_alarm_and_contact_hw(child,deviceName)
    return ret
def analysis_vpn_down(child,deviceName):
    ret = ''
    # vpnIndexList = get_vpn_down_index_list(child)
    vpnIndexList = get_all_vpn_down_list(child,deviceName)
    vpnNameList = get_cpn_name_by_index(child,vpnIndexList,deviceName)
    for i in range(len(vpnNameList)):
        ret = ret + '\n' +vpnNameList[i] + ' :'+ diagnose_vpn_by_name(child, deviceName, vpnNameList[i])
    return ret

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
    if cmdfile.find('tunnel_down') == -1:
        analysis = analysis + '\n\n\n' + '=' * 20 + '问题分析' + '=' * 20
        analysis = analysis + analysis_vpn_down(child,deviceName)
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
