#!/usr/bin/env python
#-*- coding: UTF-8 -*-
import pexpect
import argparse
import configparser
import os
import datetime
import re
import json

def arg_parse():
    parser = argparse.ArgumentParser(description='Diagnose')
    parser.add_argument("--routerId", dest='routerId', help="routerId",default="1.1.1.1", type=str)
    parser.add_argument("--deviceName", dest='deviceName', help="deviceName",default="<SHPE1>", type=str)
    parser.add_argument("--host", dest='host', help="SSH IP",default="127.0.0.1", type=str)
    parser.add_argument("--port", dest='port', help="SSH port",default="22", type=str)
    parser.add_argument("--upsrname", dest='upsrname', help="upsrname",default="root", type=str)
    parser.add_argument("--upsrword", dest='upsrword', help="upsrword",default="123456", type=str)
    parser.add_argument("--cmdfile", dest='cmdfile', help="CMD file name",default="/root/upmdc/o2/karaf-0.8.2/diagnose/cmd/vpn_down.txt", type=str)
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
    return finame

def child_expect(child,expectStr):
    try:
        child.expect(expectStr,timeout=7)
    except Exception, e:
        child.sendline("\n###               Failure diagnosis               ###")
        child.close(force=True)
        exit(2)
def analysis_business(tunnelName):
    load_f = open("/root/upmdc/o2/karaf-0.8.2/diagnose/definition.json", 'r')
    load_dict = json.load(load_f)
    dic_ww = load_dict["dic_ww"]
    dic_xy = load_dict["dic_xy"]
    dic_z = load_dict["dic_z"]
    dic_line = load_dict["dic_line"]
    ret = ''
    tunnelId = filter(str.isdigit, tunnelName)
    if len(tunnelId) < 4 or len(tunnelId) > 5:
        ret = ': 没有与之对应的业务'
    elif dic_ww.has_key(tunnelId[0:(len(tunnelId) - 3)]) and \
            dic_line.has_key(tunnelId[(len(tunnelId) - 3):(len(tunnelId) - 1)]) and \
            dic_xy.has_key(tunnelId[len(tunnelId) - 3]) and dic_xy.has_key(tunnelId[len(tunnelId) - 2]) and \
            dic_z.has_key(tunnelId[len(tunnelId) - 1]):
            ret = ': ' + dic_ww[tunnelId[0:(len(tunnelId)-3)]].encode("utf-8") + \
                  ' 的 '+ dic_line[tunnelId[(len(tunnelId)-3):(len(tunnelId)-1)]].encode("utf-8") +\
                  ', 从 ' + dic_xy[tunnelId[len(tunnelId)-3]].encode("utf-8") +\
                  ' 到 ' + dic_xy[tunnelId[len(tunnelId)-2]].encode("utf-8") + \
                  ' 的 ' + dic_z[tunnelId[len(tunnelId)-1]].encode("utf-8") + ' 业务'
    else:
        ret = ': 没有与之对应的业务'
    return ret
def analysis_alarm_hot(echo_info):
    switched_tunnel = re.findall(r'[(](.*?)[)]', echo_info)
    if len(switched_tunnel) == 0:
        return []
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
        return tunnels
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
def analysis_ospf_up(echo_info,deviceName):
    load_f = open("/root/upmdc/o2/karaf-0.8.2/diagnose/definition.json", 'r')
    load_dict = json.load(load_f)
    interfaceListMap = load_dict["interfaceListMap"]
    interfaceList = interfaceListMap['<SHPE1>']
    ospfInterfaceList = []
    ret = ''
    areas = echo_info.split('Area')
    if len(areas) == 0:
        return '\n  There is no ospf info'
    for i in range(1,len(areas)):
        area = areas[i].strip()
        areadetail = area.split(' ')
        ifName = re.findall(r'[(](.*?)[)]', area)
        ospfInterfaceList = ospfInterfaceList + ifName
        ret = ret + "\n  interface " + ''.join(ifName) + ' Area ' + areadetail[0]
        ret = ret + ' up time : ' + areadetail[-1]
        uptime = re.findall(r'\d+', areadetail[-1])
        if int(uptime[0]) >= 24 :
            ret = ret + " ，OSPF形成时间大于24小时，不是OSPF的问题"
        else:
            ret = ret + " ，OSPF形成时间小于24小时，OSPF可能有问题"
    ifDownList = list(set(interfaceList).difference(set(ospfInterfaceList)))
    for i in range(len(ifDownList)):
        ret = ret + '\n  ' + "interface " + str(ifDownList[i]) +': 没有OSPF邻居，可能有问题'
    return ret
def vpn_route_definition_get(deviceName, name):
    ret = {}
    load_f = open("/root/upmdc/o2/karaf-0.8.2/diagnose/definition.json", 'r')
    load_dict = json.load(load_f)
    deviceMap = load_dict["vpn-route"]
    vpnrouteMap = deviceMap[deviceName]
    if vpnrouteMap.has_key(name):
        echo = ''.join(vpnrouteMap[name])
        # 过滤key值
        route_dest = re.findall(r'(?:\d{1,3}\.){3}(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)/\d+', echo)
        # 按照key值分割字符串
        route_table = re.split(r'(?:\d{1,3}\.){3}(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)/\d+', echo.strip())
        for i in range(1, len(route_table)):
            nexthop = []
            # 取出每一条路由中的IP地址
            ip_next = re.findall(r'(?:\d{1,3}\.){3}(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)', route_table[i])
            # 使用IP地址进行分割
            if_next = re.split(r'(?:\d{1,3}\.){3}(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)', route_table[i].strip())
            for j in range(len(if_next)):
                tunnel = re.findall(r'Tunnel\d+', if_next[j])
                nexthop = nexthop + tunnel
            nexthop.append(if_next[-1].strip())
            ip_nexthop = []
            for k in range(len(ip_next)):
                ip_nexthop.append(' '.join([ip_next[k].encode('utf-8'),nexthop[k].encode('utf-8')]))
            ret.update({route_dest[i - 1].encode('utf-8'):ip_nexthop})
    return ret
def vpn_route_current_get(child, deviceName, name):
    ret = {}
    child.sendline('display ip routing-table vpn-instance ' + name + ' | no-more ')
    child_expect(child, deviceName)
    echo = child.before
    # 过滤key值
    route_dest = re.findall(r'(?:\d{1,3}\.){3}(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)/\d+', echo)
    # 按照key值分割字符串
    route_table = re.split(r'(?:\d{1,3}\.){3}(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)/\d+', echo.strip())
    for i in range(1, len(route_table)):
        nexthop = []
        # 取出每一条路由中的IP地址
        ip_next = re.findall(r'(?:\d{1,3}\.){3}(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)', route_table[i])
        # 使用IP地址进行分割
        if_next = re.split(r'(?:\d{1,3}\.){3}(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)', route_table[i].strip())
        for j in range(len(if_next)):
            tunnel = re.findall(r'Tunnel\d+', if_next[j])
            nexthop = nexthop + tunnel
        nexthop.append(if_next[-1].strip())
        ip_nexthop = []
        for k in range(len(ip_next)):
            ip_nexthop.append(' '.join([ip_next[k], nexthop[k]]))
        ret.update({route_dest[i - 1]: ip_nexthop})
    return ret
def get_all_vpn_down_name_list(child,deviceName):
    ret = []
    child.sendline('display ip vpn-instance verbose | no-more | include "(VPN-Instance Name and ID :)|(Vrf Status :)"')
    child_expect(child, deviceName)
    echo_string = child.before
    allVpn = echo_string.strip().split('\r\n\r\n')
    if len(allVpn) >1:
        vpndetail = allVpn[1].strip().split('VPN-Instance Name and ID :')
        for i in range(1,len(vpndetail)):
            if vpndetail[i].strip().find('Vrf Status : DOWN') != -1:
                name = vpndetail[i].strip().split(',')
                ret.append(name[0])
    return ret
def get_all_vpn_up_name_list(child,deviceName):
    ret = []
    child.sendline('display ip vpn-instance verbose | no-more | include "(VPN-Instance Name and ID :)|(Vrf Status :)"')
    child_expect(child, deviceName)
    echo_string = child.before
    allVpn = echo_string.strip().split('\r\n\r\n')
    if len(allVpn) >1:
        vpndetail = allVpn[1].strip().split('VPN-Instance Name and ID :')
        for i in range(1,len(vpndetail)):
            if vpndetail[i].strip().find('Vrf Status : DOWN') != -1:
                name = vpndetail[i].strip().split(',')
                ret.append(name[0])
    return ret
def get_tunnel_policy_by_vpnname(child,deviceName,vpnName):
    child.sendline('display ip vpn-instance verbose ' + vpnName + ' | no-more | include "Tunnel Policy :"')
    child_expect(child, deviceName)
    echo_info = child.before
    policy_line = echo_info.split('\r\n')
    if len(policy_line) == 4:
        policy = policy_line[2].split('Tunnel Policy :')
        return policy[1].strip()
    return ''
def get_tunnel_list_by_tunnel_policy(child,deviceName,policyName):
    child.sendline('display tunnel-policy ' + policyName + ' | no-more | include Tunnel')
    child_expect(child, deviceName)
    echo_info = child.before
    tunnelList = re.findall('Tunnel\d+',echo_info)
    tunnels = list(set(tunnelList))
    return tunnels
def check_tunnel_up(child,deviceName,tunnelName):
    child.sendline('display interface ' + tunnelName + ' | no-more | include "Line protocol current state :"')
    child_expect(child, deviceName)
    echo_info = child.before
    if echo_info.strip().split(':')[-1].strip() == 'UP':
        return True
    else:
        return False
def check_resvered_bind(child, deviceName, tunnelName):
    ret = True
    child.sendline('system-view ')
    child_expect(child, deviceName[1:-1]+']')
    child.sendline('interface ' + tunnelName)
    child_expect(child, deviceName[1:-1]+'-'+tunnelName+']')
    child.sendline('display this | no-more')
    child_expect(child, deviceName[1:-1]+'-'+tunnelName+']')
    echo_tunnel_info = child.before
    if echo_tunnel_info.find('mpls te reserved-for-binding') == -1:
        ret = False
    child.sendline('quit ')
    child_expect(child, deviceName[1:-1]+']')
    child.sendline('quit ')
    child_expect(child, deviceName)
    return ret
def diagnose_vpn_by_name(child,deviceName,vpnName):
    ret = ''
    policyName = get_tunnel_policy_by_vpnname(child, deviceName, vpnName)
    #1.使用命令display ip vpn-instance verbose xxx 检查tnl-policy字段是否存在。
    if policyName != '':
        #2.使用display tunnel-policy tunnel-policy-name检查隧道策略的内容。
        ret = ret + 'tnl-policy字段为 ' + policyName
        tunnelList = get_tunnel_list_by_tunnel_policy(child, deviceName, policyName)
        ret = ret + '\n    ' + policyName + ' 包含的隧道有 ' + ', '.join(tunnelList)
        for i in range(len(tunnelList)):
            if check_tunnel_up(child, deviceName, tunnelList[i]) == True:
                ret = ret + ('\n      %-13s' % tunnelList[i]) + ' 隧道状态UP，'
                # 3.进入Tunnel接口视图，执行display this命令检查Tunnel接口下是否配置了mpls te reserved-for-binding命令。
                if check_resvered_bind(child, deviceName,tunnelList[i]) == True:
                    ret = ret + '已配置绑定 '
                else:
                    #4.进入Tunnel接口视图，配置mpls te reserved-for-binding命令，然后看是否出现告警
                    ret = ret + '未配置绑定 '
            else:
                #5.检查TE接口下的配置，并根据TE相关的告警确认和排除问题，然后看是否出现告警
                ret = ret + ('\n      %-13s' % tunnelList[i]) + ' 隧道状态DOWN'
    else:
        ret = ret + 'tnl-policy字段为空'
        pass
    return ret
def vpn_route_get_diff(def_route,cur_route):
    ret = ''
    print '=define='
    print def_route
    print '=cur='
    print cur_route
    route_less = ''
    route_more = ''
    for key in def_route.keys():
        def_route_list = def_route[key]
        route_diff_less = def_route_list
        if cur_route.has_key(key):
            cur_route_list = cur_route[key]
            route_diff_less = list(set(def_route_list).difference(set(cur_route_list)))
        if len(route_diff_less) != 0:
            # route_less = route_less + '        ' + key + "\n          " + ',\n          '.join(route_diff_less) + '\n'
            route_less = route_less + '        ' + key + "\n          " + ','.join(route_diff_less) + '\n'
    for key in cur_route.keys():
        cur_route_list = cur_route[key]
        route_diff_more = cur_route_list
        if def_route.has_key(key):
            def_route_list = def_route[key]
            route_diff_more = list(set(cur_route_list).difference(set(def_route_list)))
        if len(route_diff_more) != 0:
            # route_more = route_more + '        ' + key + "\n          " + ',\n          '.join(route_diff_more) + '\n'
            route_more = route_more + '        ' + key + "\n          " + ','.join(route_diff_more) + '\n'
    if (route_less != '' or route_more != ''):
        if route_less == '':
            ret = ret + '      a)减少的路由: 无减少的路由\n'
        else:
            ret = ret + '      a)减少的路由: \n' + route_less
        if route_more == '':
            ret = ret + '      b)增加的路由: 无增加路由\n'
        else:
            ret = ret + '      b)增加的路由: \n' + route_more
    print '=diff='
    print ret
    if ret == '':
        ret = '\n    vpn路由差异：vpn路由没有差异'
    else:
        ret = '\n    vpn路由差异：\n' + ret
    return ret
def analysis_vpn_down(child,deviceName):
    ret = ''
    vpnDownList = get_all_vpn_down_name_list(child,deviceName)
    ret = ret + '\n一、处于down状态的vpn： ' + ', '.join(vpnDownList)
    vpnUpList = get_all_vpn_up_name_list(child, deviceName)
    ret = ret + '\n二、处于up状态的vpn： ' + ', '.join(vpnUpList)
    if len(vpnUpList) == 0:
        ret = ret + '\n  没有处于up状态的vpn'
    for i in range(len(vpnUpList)):
        ret = ret + '\n  ' +vpnUpList[i] + ':'
        print '=====================================' + vpnUpList[i]
        def_route = vpn_route_definition_get(deviceName, vpnUpList[i])
        cur_route = vpn_route_current_get(child, deviceName, vpnUpList[i])
        ret = ret + vpn_route_get_diff(def_route, cur_route)
    return ret

def pexpect_execmd(hostname, deviceName, username, password, cmdfile):
    child = pexpect.spawn('ssh  -o StrictHostKeyChecking=no %s@%s' % (username,hostname))
    child_expect(child,'(?i)ssword:')
    child.sendline("%s" % password)
    child_expect(child,"(?i)N]:")
    child.sendline("n")
    child_expect(child,deviceName)
    child.sendline("###  The following is the diagnostic information  ###")
    finame = create_file()
    # child.logfile = file(finame, 'w')
    child_expect(child,deviceName)
    f = open(cmdfile)
    line = f.readlines()
    cmd_index = 0
    analysis = ''
    if cmdfile.find('vpn_down') != -1:
        analysis = analysis + '=' * 20 + 'VPN Down问题分析' + '=' * 20
        analysis = analysis + analysis_vpn_down(child,deviceName)
    else:
        analysis = analysis + '='*20 + '隧道切换问题分析' + '='*20 + '\n'
        analysis = analysis + '第一步: 查看当前处于切换状态的隧道'
        child.sendline('display alarm active verbose | no-more | include "primary LSP to the hot-standby"')
        child_expect(child,deviceName)
        tunnelList = analysis_alarm_hot(child.before)
        if (len(tunnelList)) != 0:
            analysis = analysis + '\n  ' +  ', '.join(tunnelList)
            analysis = analysis + '\n第二步: 查看ospf邻居的up时间'
            child.sendline('display ospf peer | no-more | include "(Neighbor is up for) | (Router ID: )"')
            child_expect(child,deviceName)
            analysis = analysis + analysis_ospf_up(child.before,deviceName)
            analysis = analysis + '\n第三步: 分析业务影响'
            for i in range(len(tunnelList)):
                analysis = analysis + '\n  ' + tunnelList[i] + analysis_business(tunnelList[i])
                #analysis = analysis + analysis_alarm_bfd(child.before)
                #analysis = analysis + analysis_logbuffer_hot(child.before)
        else:
            analysis = analysis + '\n  没有处于切换状态的隧道，结束检查'
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
    cmdfile = args.cmdfile
    deviceName = args.deviceName
    deviceCfg = configparser.ConfigParser()
    deviceCfg.read('/root/upmdc/o2/karaf-0.8.2/sr_conf.ini')

    for i, val in enumerate(deviceCfg.sections()):
        if deviceCfg.get((val), "routerId") == routerId:
            host = deviceCfg.get((val), "sshIP")
            upsrname = deviceCfg.get((val), "userName")
            upsrword = deviceCfg.get((val), "passWord")
            pexpect_execmd(host, deviceName, upsrname, upsrword, cmdfile)
            break
