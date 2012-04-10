var NAVTREE =
[
  [ "Social-Wombat", "index.html", [
    [ "Class List", "annotated.html", [
      [ "security.AES", "classsecurity_1_1_a_e_s.html", null ],
      [ "test.AESTest", "classtest_1_1_a_e_s_test.html", null ],
      [ "security.CheckSum", "classsecurity_1_1_check_sum.html", null ],
      [ "test.CheckSumTest", "classtest_1_1_check_sum_test.html", null ],
      [ "storage.ClassData", "classstorage_1_1_class_data.html", null ],
      [ "storage.ClassDB", "classstorage_1_1_class_d_b.html", null ],
      [ "test.ClassDBTest", "classtest_1_1_class_d_b_test.html", null ],
      [ "storage.ClassList", "classstorage_1_1_class_list.html", null ],
      [ "storage.ClassRoom", "classstorage_1_1_class_room.html", null ],
      [ "client.Client", "classclient_1_1_client.html", null ],
      [ "storage.Cookie", "classstorage_1_1_cookie.html", null ],
      [ "hub.Hub", "classhub_1_1_hub.html", null ],
      [ "hub.HubSocketHandler", "classhub_1_1_hub_socket_handler.html", null ],
      [ "test.HubTest", "classtest_1_1_hub_test.html", null ],
      [ "test.HubTester", "classtest_1_1_hub_tester.html", null ],
      [ "storage.Log", "classstorage_1_1_log.html", null ],
      [ "storage.Message", "classstorage_1_1_message.html", null ],
      [ "storage.Post", "classstorage_1_1_post.html", null ],
      [ "server.Server", "classserver_1_1_server.html", null ],
      [ "storage.ServerList.ServerData", "classstorage_1_1_server_list_1_1_server_data.html", null ],
      [ "storage.ServerList", "classstorage_1_1_server_list.html", null ],
      [ "server.ServerSocketHandler", "classserver_1_1_server_socket_handler.html", null ],
      [ "test.ServerTester", "classtest_1_1_server_tester.html", null ],
      [ "util.SocketPackage", "classutil_1_1_socket_package.html", null ],
      [ "server.StartServers", "classserver_1_1_start_servers.html", null ],
      [ "util.StringLegalityChecker", "classutil_1_1_string_legality_checker.html", null ],
      [ "test.StringLegalityCheckerTest", "classtest_1_1_string_legality_checker_test.html", null ],
      [ "hub.SysAdminInterface", "classhub_1_1_sys_admin_interface.html", null ],
      [ "hub.SystemLogin", "classhub_1_1_system_login.html", null ],
      [ "test.SystemTest", "classtest_1_1_system_test.html", null ],
      [ "test.TestClient", "classtest_1_1_test_client.html", null ],
      [ "test.TestHub", "classtest_1_1_test_hub.html", null ],
      [ "storage.UserList.User", "classstorage_1_1_user_list_1_1_user.html", null ],
      [ "client.UserInterface", "classclient_1_1_user_interface.html", null ],
      [ "util.UserInterfaceHelper", "classutil_1_1_user_interface_helper.html", null ],
      [ "test.UserInterfaceTest", "classtest_1_1_user_interface_test.html", null ],
      [ "storage.UserList", "classstorage_1_1_user_list.html", null ]
    ] ],
    [ "Class Index", "classes.html", null ],
    [ "Class Members", "functions.html", null ]
  ] ]
];

function createIndent(o,domNode,node,level)
{
  if (node.parentNode && node.parentNode.parentNode)
  {
    createIndent(o,domNode,node.parentNode,level+1);
  }
  var imgNode = document.createElement("img");
  if (level==0 && node.childrenData)
  {
    node.plus_img = imgNode;
    node.expandToggle = document.createElement("a");
    node.expandToggle.href = "javascript:void(0)";
    node.expandToggle.onclick = function() 
    {
      if (node.expanded) 
      {
        $(node.getChildrenUL()).slideUp("fast");
        if (node.isLast)
        {
          node.plus_img.src = node.relpath+"ftv2plastnode.png";
        }
        else
        {
          node.plus_img.src = node.relpath+"ftv2pnode.png";
        }
        node.expanded = false;
      } 
      else 
      {
        expandNode(o, node, false);
      }
    }
    node.expandToggle.appendChild(imgNode);
    domNode.appendChild(node.expandToggle);
  }
  else
  {
    domNode.appendChild(imgNode);
  }
  if (level==0)
  {
    if (node.isLast)
    {
      if (node.childrenData)
      {
        imgNode.src = node.relpath+"ftv2plastnode.png";
      }
      else
      {
        imgNode.src = node.relpath+"ftv2lastnode.png";
        domNode.appendChild(imgNode);
      }
    }
    else
    {
      if (node.childrenData)
      {
        imgNode.src = node.relpath+"ftv2pnode.png";
      }
      else
      {
        imgNode.src = node.relpath+"ftv2node.png";
        domNode.appendChild(imgNode);
      }
    }
  }
  else
  {
    if (node.isLast)
    {
      imgNode.src = node.relpath+"ftv2blank.png";
    }
    else
    {
      imgNode.src = node.relpath+"ftv2vertline.png";
    }
  }
  imgNode.border = "0";
}

function newNode(o, po, text, link, childrenData, lastNode)
{
  var node = new Object();
  node.children = Array();
  node.childrenData = childrenData;
  node.depth = po.depth + 1;
  node.relpath = po.relpath;
  node.isLast = lastNode;

  node.li = document.createElement("li");
  po.getChildrenUL().appendChild(node.li);
  node.parentNode = po;

  node.itemDiv = document.createElement("div");
  node.itemDiv.className = "item";

  node.labelSpan = document.createElement("span");
  node.labelSpan.className = "label";

  createIndent(o,node.itemDiv,node,0);
  node.itemDiv.appendChild(node.labelSpan);
  node.li.appendChild(node.itemDiv);

  var a = document.createElement("a");
  node.labelSpan.appendChild(a);
  node.label = document.createTextNode(text);
  a.appendChild(node.label);
  if (link) 
  {
    a.href = node.relpath+link;
  } 
  else 
  {
    if (childrenData != null) 
    {
      a.className = "nolink";
      a.href = "javascript:void(0)";
      a.onclick = node.expandToggle.onclick;
      node.expanded = false;
    }
  }

  node.childrenUL = null;
  node.getChildrenUL = function() 
  {
    if (!node.childrenUL) 
    {
      node.childrenUL = document.createElement("ul");
      node.childrenUL.className = "children_ul";
      node.childrenUL.style.display = "none";
      node.li.appendChild(node.childrenUL);
    }
    return node.childrenUL;
  };

  return node;
}

function showRoot()
{
  var headerHeight = $("#top").height();
  var footerHeight = $("#nav-path").height();
  var windowHeight = $(window).height() - headerHeight - footerHeight;
  navtree.scrollTo('#selected',0,{offset:-windowHeight/2});
}

function expandNode(o, node, imm)
{
  if (node.childrenData && !node.expanded) 
  {
    if (!node.childrenVisited) 
    {
      getNode(o, node);
    }
    if (imm)
    {
      $(node.getChildrenUL()).show();
    } 
    else 
    {
      $(node.getChildrenUL()).slideDown("fast",showRoot);
    }
    if (node.isLast)
    {
      node.plus_img.src = node.relpath+"ftv2mlastnode.png";
    }
    else
    {
      node.plus_img.src = node.relpath+"ftv2mnode.png";
    }
    node.expanded = true;
  }
}

function getNode(o, po)
{
  po.childrenVisited = true;
  var l = po.childrenData.length-1;
  for (var i in po.childrenData) 
  {
    var nodeData = po.childrenData[i];
    po.children[i] = newNode(o, po, nodeData[0], nodeData[1], nodeData[2],
        i==l);
  }
}

function findNavTreePage(url, data)
{
  var nodes = data;
  var result = null;
  for (var i in nodes) 
  {
    var d = nodes[i];
    if (d[1] == url) 
    {
      return new Array(i);
    }
    else if (d[2] != null) // array of children
    {
      result = findNavTreePage(url, d[2]);
      if (result != null) 
      {
        return (new Array(i).concat(result));
      }
    }
  }
  return null;
}

function initNavTree(toroot,relpath)
{
  var o = new Object();
  o.toroot = toroot;
  o.node = new Object();
  o.node.li = document.getElementById("nav-tree-contents");
  o.node.childrenData = NAVTREE;
  o.node.children = new Array();
  o.node.childrenUL = document.createElement("ul");
  o.node.getChildrenUL = function() { return o.node.childrenUL; };
  o.node.li.appendChild(o.node.childrenUL);
  o.node.depth = 0;
  o.node.relpath = relpath;

  getNode(o, o.node);

  o.breadcrumbs = findNavTreePage(toroot, NAVTREE);
  if (o.breadcrumbs == null)
  {
    o.breadcrumbs = findNavTreePage("index.html",NAVTREE);
  }
  if (o.breadcrumbs != null && o.breadcrumbs.length>0)
  {
    var p = o.node;
    for (var i in o.breadcrumbs) 
    {
      var j = o.breadcrumbs[i];
      p = p.children[j];
      expandNode(o,p,true);
    }
    p.itemDiv.className = p.itemDiv.className + " selected";
    p.itemDiv.id = "selected";
    $(window).load(showRoot);
  }
}

