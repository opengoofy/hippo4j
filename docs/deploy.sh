#!/usr/bin/env sh

# 确保脚本抛出遇到的错误
set -e

# 生成静态文件
npm run build

# 进入生成的文件夹
cd build/

echo 'hippo4j.cn' > CNAME

git init
git add -A
git commit -m "auto commit"

# github
GIT_SSH_COMMAND="ssh -i ~/.ssh/hippo4j" git remote add origin git@github.com:hippo4j/hippo4j.github.io.git
GIT_SSH_COMMAND="ssh -i ~/.ssh/hippo4j" git push -u origin main -f

cd -
rm -rf build/
