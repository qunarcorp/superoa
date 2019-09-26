<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<head>

  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title>邮件模板</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
</head>

<body style="font-size: 16px; height:600px; color: #2f2936; padding: 0; font-family: &quot;Lato&quot;, &quot;Helvetica Neue&quot;, helvetica, sans-serif; background-image: url(https://ops-superoacp.qunarzz.com/ops_superoa_ops_superoa/prod/chengyan.liang/2019/01/24/12:10:06/mailbg.png); -webkit-font-smoothing: antialiased; width: 100%; font-weight: 300; margin: 0; background-color: #fff">

<table align="center" border="0" cellpadding="0" cellspacing="0" width="700"
       style="border-collapse: collapse; word-break: break-all">
  <tr style="background: #4a4d54; height: 35px">
    <td style="padding: 12px; line-height: 35px;" colspan="2">
      <img
          src="https://ops-superoacp.qunarzz.com/ops_superoa_ops_superoa/prod/chengyan.liang/2018/12/25/16:01:34/oa.png"
          alt="logo" width="35" height="35" style="margin: auto; float: left"/>
      <a style="font-size: 16px; color: white; text-decoration:none; margin-left: 18px">
        QunarOA通知
      </a>
    </td>
  </tr>
  <tr style="background: #e4e4e4">
    <td colspan="2" style="padding: 12px">
      <p style="font-size: 17px; color: #666">以下为 <b>${applyUserName}</b> ${suffix}：<br><br><a
          href=${applyLink}>${title}</a></p>
      <p style="margin-top: 24px"><span
          style="font-weight: bold; font-size: 20px;color:#555">摘要</span></p>
      <div style="font-size: 14px; color: #555">
      <#if content?? && (content?size>0)>
      <#list content as contentLine>
        <span><b>${contentLine[0]!""}</b> : ${contentLine[1]!"暂无详细信息"}</span><br>
      </#list>
      <#else>
        <span>暂无详细信息</span><br>
      </#if>
      </div>
      <p style="font-size: 12px; color: #888; text-align: center; margin-top: 40px;">*
        前往审批申请，请点击蓝色链接，谢谢！</p>
    </td>
  </tr>
  <tr style="background: #4a4d54; height: 160px">
    <td>
      <div style="position: relative; margin-top: 1px; color: white; margin-left: 20px">
        <div style="margin-bottom: 10px;margin-top: 10px;">
          <img
              src="https://ops-superoacp.qunarzz.com/ops_superoa_ops_superoa/prod/chengyan.liang/2018/12/25/16:01:34/oa.png"
              alt="QunarLogo" width="120" height="120"/>
        </div>
        <div style="font-size: 13px">友情链接：
          <a style="text-decoration: none; color: white"
             href="http://127.0.0.1:8080//">OA系统</a>
        </div>
      </div>
    </td>
    <td>
      <div style="width: 100px;  float: right; margin-right: 20px; text-align: center">
        <img
            src=${QRCodeUrl}
            alt="QunarOA审批" width="90" height="90"/>
        <div style="font-size: 12px; color: white; margin-top: 8px">审批扫一扫</div>
      </div>
    </td>
  </tr>
</table>
</body>

</html>