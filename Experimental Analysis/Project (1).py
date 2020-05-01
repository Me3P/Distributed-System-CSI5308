#!/usr/bin/env python
# coding: utf-8

# In[1]:


import time
import random 

graph = {}
values = {}
edges = []
class Edge(object):
    """Each has 2 nodes and a value which is the value that source(respectivly taget) node send during Yo_down(resppectivly Yo_up)"""
    def __init__(self, source, target):
        self.source = source
        self.target = target
        self.value = 99999999999999
        self.color = "#EA1B56"

    def __repr__(self):
        return str(self.source.ID) + ' ' + str(self.target.ID)
    def __str__(self):
        return str(self.source.ID) + ' ' + str(self.target.ID)

    def remove(self):
        self.source.edges.remove(self)
        self.target.edges.remove(self)
        self.source.remove_check()
        self.target.remove_check()
        
        edges.remove(self)

    def flip(self):
        self.source, self.target = self.target, self.source


class Node(object):
    """Node has ID, minimum value and list of ajdacent edges"""
    def __init__(self, ID):
        self.ID = ID
        self.minimum = ID
        self.edges = []
        self.color = None

    def remove_check(self):
        if len(self.edges) == 0:
            del nodes[self.ID]
            
    def is_sink(self):
        for edge in self.edges:
            if edge.source == self:
                return False
        return True
    
    def has_only_one_edge(self):
        return len(self.edges) == 1

            


# In[2]:



def read_data(data):
    nodes = {}
    edges = []
    n, m = list(map(int, str(data[0]).split()))
    for i in range(m):
        u, w = list(map(int, str(data[i+1]).split()))
        if u > w : u, w = w, u
        if u not in nodes:
            nodes[u] = Node(u)
        if w not in nodes:
            nodes[w] = Node(w)
        edge = Edge(nodes[u], nodes[w])
        edges.append(edge)    
        nodes[u].edges.append(edge)
        nodes[w].edges.append(edge)
    return nodes, edges


def initial(graph, state):
    if state == 'Yo_down' : val = 1
    else :val = 0
    incoming_edges = {}
    for node in graph.values():
        incoming_edges[node] = 0
    for node in graph.values():
        for edge in node.edges:
            if val == 1 and edge.target == node:
                incoming_edges[edge.target] += 1
            elif val == 0 and edge.source == node:
                incoming_edges[edge.source] += 1

    res = []
    for node in graph.values():
        if incoming_edges[node] == 0:
            res.append(node)
            node.color = "#2D9184"
    return res, incoming_edges


# In[3]:


def run_down(graph):
    val = 1
    lst, incoming_edges = initial(graph, 'Yo_down')
    total_msgs = 0
    while len(lst) != 0:
        node = lst.pop(0)
        for edge in node.edges:
            if edge.source == node:
                total_msgs += 1
                adj = edge.target
                adj.minimum = min(node.minimum, adj.minimum) # value of the target node
                edge.value = node.minimum # value on edge
                incoming_edges[adj] -= 1
                if incoming_edges[adj] == 0:
                    lst.append(adj)
    return total_msgs
                

def run_up(graph):
    lst, incoming_edges = initial(graph, 'Yo_up')
    total_flips, total_msgs = 0, 0
    while len(lst) != 0:
        node = lst.pop(0)
        yes_lst = []
        no_lst = []
        for edge in node.edges:
            if edge.target == node and incoming_edges[edge.source] != 0:
                total_msgs += 1
                adj = edge.source
                incoming_edges[adj] -= 1
                if incoming_edges[adj] == 0:
                    lst.append(adj)
                if edge.value > node.minimum:
                    no_lst.append(edge)
                    adj.minimum = min(node.minimum, adj.minimum)
                else:
                    yes_lst.append(edge)
        total_flips += len(no_lst)
        for edge in no_lst:
            edge.flip()

        if not node.is_sink() : yes_lst = yes_lst[:-1]
        for edge in yes_lst:
            edge.remove()

    return total_flips, total_msgs


# In[4]:


def algorithm(data):
    global edges, nodes
    total_flips = 0
    total_steps = 0
    total_msgs = 0
    nodes, edges = read_data(data)
    while len(edges) != 0:
        msgs = run_down(nodes)
        total_msgs += msgs
        flips, msgs = run_up(nodes)
        total_flips += flips
        total_msgs += msgs
        total_steps += 1
    return [total_flips, total_msgs, total_steps]
data = ['9 8', '1 6', '2 6', '2 7', '3 7', '3 8', '4 8', '4 9', '5 9']
algorithm(data)


# In[5]:


def generate_graph(n, m):
    m = min(max(m, n-1), n*(n-1)//2)
    lst = ["{} {}".format(n, m)]
    edges = []
    n_node = [i for i in range(2, n+1)]
    cur_node = [1]
    for i in range(n-1):
        sc = cur_node[random.randint(0, len(cur_node)-1)]
        tg = n_node[random.randint(0, len(n_node)-1)]
        cur_node.append(tg)
        n_node.remove(tg)
        lst.append("{} {}".format(sc, tg))
    for i in range(1, n+1):
        for j in range(i+1, n+1):
            if "{} {}".format(i, j) not in lst:
                edges.append("{} {}".format(i, j))
    for i in range(m - (n-1)):
        lst.append(edges.pop(random.randint(0, len(edges) - 1)))
    return lst

def generate_graph_big(n, m):
    d = {}
    m = min(max(m, n-1), n*(n-1)//2)
    lst = ["{} {}".format(n, m)]
    edges = []
    n_node = [i for i in range(2, n+1)]
    cur_node = [1]
    for i in range(n-1):
        sc = cur_node[random.randint(0, len(cur_node)-1)]
        tg = n_node[random.randint(0, len(n_node)-1)]
        cur_node.append(tg)
        n_node.remove(tg)
        lst.append("{} {}".format(sc, tg))
        d[(sc, tg)] = True
    
    co = 0
    while co != m - (n-1):
        sc = random.randint(1, n)
        tg = random.randint(1, n)
        if sc != tg and (sc, tg) not in d and (tg, sc) not in d:
            lst.append("{} {}".format(sc, tg))
            d[(sc, tg)] = True
            co += 1
    return lst
    
generate_graph_big(10, 15) 


# ## if we fix the number of nodes and increase the number of edges you can see that the number of messages increases linearly so we can conclude that the message complexity of the algorithm is related to the number of edges
# ## We can not conclude anything from the number of flipped edges in this case
# ## number of step is surprisingly low and as we increase the number of edges it gets less and less

# In[6]:


import numpy as np
import matplotlib.pyplot as plt

t = []
steps, msgs, flips = [], [], []
for i in range(33, 450):
    flip, msg, step = algorithm(generate_graph(32, i))
    steps.append(step)
    msgs.append(msg)
    flips.append(flip)
    t.append(i)
    mx = max(msgs)


names = ['number of flips', 'number of msgs ', 'number of steps']


plt.figure(figsize=(18, 6))

plt.subplot(131)
plt.scatter(t, flips)
plt.title(names[0])

plt.subplot(132)
plt.plot(t, msgs)
plt.title(names[1])

plt.subplot(133)
plt.plot(t, steps)
plt.title(names[2])

plt.show()


# # In this scenario number of edges are fixed and we increase the number of nodes
# ## It looks the number of flipping increase linearly until we reach 225 nodes which are half of the number of edges
# ## surprisingly the behaviour of the number of messages and number of flipping edges are the same when the number of edges is fixed!!!
# ## In the third figure we can see that number of step increase when the number of the edge is close to the number of nodes, we can anticipate that the maximum number of steps happens when we have a sparse graph.

# In[7]:


t = []
steps, msgs, flips = [], [], []
for i in range(33, 450):
    flip, msg, step = algorithm(generate_graph(i, 450))
    steps.append(step)
    msgs.append(msg)
    flips.append(flip)
    t.append(i)
    mx = max(flips)


names = ['number of flips', 'number of msgs ', 'number of steps']


plt.figure(figsize=(18, 6))

plt.subplot(131)
plt.scatter(t, flips)
plt.title(names[0])

plt.subplot(132)
plt.plot(t, msgs)
plt.title(names[1])

plt.subplot(133)
plt.plot(t, steps)
plt.title(names[2])


plt.show()


# # In this case, we compare one small and 2 medium graphs from the lowest number of edges(tree) to highest number of edges( complete graph)
# ## the only thing we can say about this case is that the number of messages is related to the number of edges linearly.
# ## Also the number of steps is really low in random cases.
# 

# In[8]:


import math
t = []
steps, msgs, flips = [], [], []
for j in [32, 128 ,256]:
    t1 = []
    steps1, msgs1, flips1 = [], [], []
    for i in range(j, j*(j-1)//2, max(j*(j-1)//100,1)):
        flip, msg, step = algorithm(generate_graph(j, i))
        steps1.append(step)
        msgs1.append(msg)
        flips1.append(flip)
        t1.append(i)
        mx = max(flips1)
    t.append(t1)
    steps.append(steps1)
    msgs.append(msgs1)
    flips.append(flips1)

    

names = ['number of flips', 'number of msgs ', 'number of steps']


plt.figure(figsize=(18, 18))

plt.subplot(331)
plt.scatter(t[0], flips[0])
plt.ylabel(names[0])
plt.title(names[0])

plt.subplot(332)
plt.plot(t[0], msgs[0])
plt.title(names[1])

plt.subplot(333)
plt.plot(t[0], steps[0])
plt.title(names[2])

plt.subplot(334)
plt.scatter(t[1], flips[1])
plt.ylabel(names[1])


plt.subplot(335)
plt.plot(t[1], msgs[1])
plt.subplot(336)
plt.plot(t[1], steps[1])

plt.subplot(337)
plt.scatter(t[2], flips[2])
plt.ylabel(names[2])

plt.subplot(338)
plt.plot(t[2], msgs[2])
plt.subplot(339)
plt.plot(t[2], steps[2])

plt.show()


# # in this case, we have one small one medium and one large graph, and we study the case when the number of edges is low.
# ## in the third case (large graph) when the number of edges is low, the number of flippling edges has linear behaviour.
# ## Moreover, the total number of messages grows linearly with the number of edges
# 

# In[9]:


import math
t = []
steps, msgs, flips = [], [], []
for j in [32, 256 , 4096]:
    t1 = []
    steps1, msgs1, flips1 = [], [], []
    for i in range(j, j*5 ,j//10):
        graph = generate_graph_big(j, i)
        flip, msg, step = algorithm(graph)
        steps1.append(step)
        msgs1.append(msg)
        flips1.append(flip)
        t1.append(i)
        mx = max(flips1)
    t.append(t1)
    steps.append(steps1)
    msgs.append(msgs1)
    flips.append(flips1)
    
    

names = ['number of flips', 'number of msgs ', 'number of steps']


plt.figure(figsize=(18, 18))

plt.subplot(331)
plt.scatter(t[0], flips[0])
plt.ylabel(names[0])
plt.title(names[0])

plt.subplot(332)
plt.plot(t[0], msgs[0])
plt.title(names[1])

plt.subplot(333)
plt.plot(t[0], steps[0])
plt.title(names[2])

plt.subplot(334)
plt.scatter(t[1], flips[1])
plt.ylabel(names[1])


plt.subplot(335)
plt.plot(t[1], msgs[1])
plt.subplot(336)
plt.plot(t[1], steps[1])

plt.subplot(337)
plt.scatter(t[2], flips[2])
plt.ylabel(names[2])

plt.subplot(338)
plt.plot(t[2], msgs[2])
plt.subplot(339)
plt.plot(t[2], steps[2])

plt.show()

