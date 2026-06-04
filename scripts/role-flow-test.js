const { execFileSync } = require('child_process');
const http = require('http');

const BASE = 'http://127.0.0.1:8082/gift-menu-api';
const run = `${Date.now()}`;
const users = {
  parent: { openId: `ut_parent_${run}`, nickName: '测试家长', role: 'parent' },
  child: { openId: `ut_child_${run}`, nickName: '测试孩子', role: 'child' },
  relative: { openId: `ut_relative_${run}`, nickName: '测试亲友', role: 'relative' },
  outsider: { openId: `ut_outsider_${run}`, nickName: '测试外人', role: 'relative' },
  friendA: { openId: `ut_friend_a_${run}`, nickName: '测试朋友A', role: 'relative' },
  friendB: { openId: `ut_friend_b_${run}`, nickName: '测试朋友B', role: 'relative' }
};

const cases = [];

function mysql(sql) {
  execFileSync('mysql', ['-h127.0.0.1', '-uroot', 'my_gift_menu', '-e', sql], {
    env: { ...process.env, MYSQL_PWD: '123456' },
    stdio: ['ignore', 'pipe', 'pipe']
  });
}

async function post(path, data) {
  const bodyText = JSON.stringify(data || {});
  return new Promise((resolve, reject) => {
    const req = http.request(BASE + path, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(bodyText)
      }
    }, res => {
      const chunks = [];
      res.on('data', chunk => chunks.push(chunk));
      res.on('end', () => {
        const text = Buffer.concat(chunks).toString('utf8');
        let body = {};
        try {
          body = text ? JSON.parse(text) : {};
        } catch (error) {
          body = { raw: text };
        }
        resolve({ httpStatus: res.statusCode, body });
      });
    });
    req.on('error', reject);
    req.write(bodyText);
    req.end();
  });
}

async function expectOk(name, fn, check) {
  try {
    const result = await fn();
    const ok = result.body && result.body.code === 200 && (!check || check(result.body.data, result));
    cases.push({ name, ok, response: result.body });
    if (!ok) throw new Error(name);
    return result.body.data;
  } catch (error) {
    cases.push({ name, ok: false, error: error.message });
    throw error;
  }
}

async function expectFail(name, fn, messagePart) {
  try {
    const result = await fn();
    const msg = result.body && (result.body.msg || result.body.message || '');
    const ok = result.body && result.body.code !== 200 && (!messagePart || msg.includes(messagePart));
    cases.push({ name, ok, response: result.body });
    if (!ok) throw new Error(`${name}: expected fail, got ${JSON.stringify(result.body)}`);
  } catch (error) {
    cases.push({ name, ok: false, error: error.message });
    throw error;
  }
}

function seedUsers() {
  const values = Object.values(users).map(user => {
    const now = 'NOW()';
    return `('${user.openId}', '', '', '${user.nickName}', '', '${user.role}', ${now}, ${now})`;
  }).join(',');
  mysql(`INSERT INTO t_gift_user (open_id, session_key, union_id, nick_name, avatar_url, role, create_time, modify_time) VALUES ${values}`);
}

async function main() {
  seedUsers();

  const family = await expectOk('家长创建家庭圈', () => post('/family/create', {
    openId: users.parent.openId,
    familyName: `家庭圈-${run}`,
    circleType: 'family',
    ownerRole: 'parent'
  }), data => data && data.id && data.circleType === 'family');

  await expectOk('我的圈子列表包含家庭圈', () => post('/family/listMine', {
    openId: users.parent.openId
  }), data => Array.isArray(data) && data.some(item => item.id === family.id && item.memberRole === 'parent'));

  const secondFamily = await expectOk('家长可以继续新建第二个圈子', () => post('/family/create', {
    openId: users.parent.openId,
    familyName: `第二圈-${run}`,
    circleType: 'friends'
  }), data => data && data.id && data.id !== family.id);

  await expectOk('下拉可拿到多个圈子', () => post('/family/listMine', {
    openId: users.parent.openId
  }), data => Array.isArray(data) && data.length >= 2 && data.some(item => item.id === secondFamily.id));

  const invite = await expectOk('家长生成 3 天游邀请码', () => post('/family/invite/generate', {
    openId: users.parent.openId,
    familyId: family.id
  }), data => data && data.inviteCode);

  const childApply = await expectOk('孩子输入邀请码提交加入申请', () => post('/family/invite/code/join', {
    openId: users.child.openId,
    inviteCode: invite.inviteCode,
    requestedRole: 'child'
  }), data => data && data.status === 'pending');

  await expectOk('家长同意孩子加入', () => post('/family/join-request/approve', {
    openId: users.parent.openId,
    requestId: childApply.id
  }), data => data && data.id === family.id);

  await expectOk('孩子按申请身份加入家庭圈', () => post('/family/listMine', {
    openId: users.child.openId
  }), data => Array.isArray(data) && data.some(item => item.id === family.id && item.memberRole === 'child'));

  const relativeApply = await expectOk('亲友输入邀请码提交加入申请', () => post('/family/invite/code/join', {
    openId: users.relative.openId,
    inviteCode: invite.inviteCode,
    requestedRole: 'relative'
  }), data => data && data.status === 'pending');

  await expectOk('孩子作为圈内成员同意亲友加入', () => post('/family/join-request/approve', {
    openId: users.child.openId,
    requestId: relativeApply.id
  }), data => data && data.id === family.id);

  const wish = await expectOk('孩子自己发布家庭圈愿望后进入待审核', () => post('/gift/create', {
    openId: users.child.openId,
    familyId: family.id,
    receiverOpenId: users.child.openId,
    receiverName: users.child.nickName,
    title: `测试愿望-${run}`,
    reason: '想要验证流程',
    sceneType: '节日',
    budget: '100'
  }), data => data && data.status === 'pending_review');

  await expectFail('孩子不能审核自己的家庭圈愿望', () => post('/gift/approve', {
    openId: users.child.openId,
    giftRequestId: wish.id
  }), '家长');

  await expectOk('家长审核通过孩子愿望', () => post('/gift/approve', {
    openId: users.parent.openId,
    giftRequestId: wish.id
  }), data => data && data.status === 'open');

  await expectFail('许愿人不能认领自己的愿望', () => post('/gift/claim', {
    openId: users.child.openId,
    giftRequestId: wish.id,
    comment: users.child.nickName
  }), '不能认领自己');

  await expectFail('圈外人不能认领圈内愿望', () => post('/gift/claim', {
    openId: users.outsider.openId,
    giftRequestId: wish.id,
    comment: users.outsider.nickName
  }), '圈内');

  await expectOk('亲友认领愿望', () => post('/gift/claim', {
    openId: users.relative.openId,
    giftRequestId: wish.id,
    comment: users.relative.nickName,
    claimNote: '我来准备'
  }), data => data && data.status === 'claimed' && data.claimedByOpenId === users.relative.openId);

  await expectFail('亲友不能确认自己的认领', () => post('/gift/confirm', {
    openId: users.relative.openId,
    giftRequestId: wish.id
  }), '家长');

  await expectOk('家长确认认领', () => post('/gift/confirm', {
    openId: users.parent.openId,
    giftRequestId: wish.id
  }), data => data && data.status === 'confirmed');

  await expectOk('家长确认礼物已收到', () => post('/gift/complete', {
    openId: users.parent.openId,
    giftRequestId: wish.id
  }), data => data && data.status === 'feedback_pending');

  await expectOk('孩子填写礼物反馈', () => post('/gift/feedback', {
    openId: users.child.openId,
    giftRequestId: wish.id,
    rating: '很喜欢',
    message: '谢谢，流程跑通了',
    preference: '下次还想收到类似的'
  }), data => data && data.status === 'feedback_done' && data.feedbackRating === '很喜欢');

  const friendsCircle = await expectOk('朋友创建朋友圈', () => post('/family/ensure', {
    openId: users.friendA.openId,
    familyName: `朋友圈-${run}`,
    circleType: 'friends'
  }), data => data && data.id && data.circleType === 'friends');

  await expectOk('朋友B加入朋友圈', () => post('/family/join', {
    openId: users.friendB.openId,
    familyId: friendsCircle.id,
    role: 'relative'
  }), data => data && data.id === friendsCircle.id);

  const friendWish = await expectOk('朋友圈愿望发布后直接可认领', () => post('/gift/create', {
    openId: users.friendA.openId,
    familyId: friendsCircle.id,
    receiverOpenId: users.friendA.openId,
    receiverName: users.friendA.nickName,
    title: `朋友愿望-${run}`,
    reason: '无需家长审核',
    sceneType: '生日'
  }), data => data && data.status === 'open');

  await expectOk('朋友圈成员可认领朋友愿望', () => post('/gift/claim', {
    openId: users.friendB.openId,
    giftRequestId: friendWish.id,
    comment: users.friendB.nickName
  }), data => data && data.status === 'claimed');

  const proposal = await expectOk('亲友发起送礼提案不指定收礼人', () => post('/proposal/create', {
    openId: users.relative.openId,
    familyId: family.id,
    senderName: users.relative.nickName,
    title: `送礼提案-${run}`,
    reason: '想送给分享对象确认',
    sceneType: '节日',
    giftOptions: JSON.stringify([
      { name: '科学实验盒', link: '' },
      { name: '绘本套装', link: '' }
    ])
  }), data => data && data.status === 'pending_confirm' && !data.receiverOpenId && data.giftOptions);

  await expectFail('送礼发起人不能自己确认提案', () => post('/proposal/confirm', {
    openId: users.relative.openId,
    proposalId: proposal.id
  }), '发起人');

  await expectFail('圈外人不能确认送礼提案', () => post('/proposal/confirm', {
    openId: users.outsider.openId,
    proposalId: proposal.id
  }), '圈内');

  await expectOk('被分享到的圈内成员确认送礼提案', () => post('/proposal/confirm', {
    openId: users.child.openId,
    proposalId: proposal.id,
    confirmNote: '可以送',
    selectedOptions: JSON.stringify([{ name: '科学实验盒', link: '' }])
  }), data => data && data.status === 'confirmed' && data.confirmOpenId === users.child.openId && data.selectedOptions);

  const failed = cases.filter(item => !item.ok);
  console.log(JSON.stringify({ ok: failed.length === 0, total: cases.length, failed, cases }, null, 2));
  if (failed.length) process.exit(1);
}

main().catch(error => {
  console.error(JSON.stringify({ ok: false, error: error.message, cases }, null, 2));
  process.exit(1);
});
