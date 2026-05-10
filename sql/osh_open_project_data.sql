-- 开源项目初始化数据
-- 项目均为 GitHub 上真实存在的知名开源项目
-- star_count / fork_count 为写入时近似值，后续由定时任务自动同步更新

INSERT INTO `osh_open_project`
    (`project_name`, `project_desc`, `project_url`, `author_name`, `project_cover`,
     `status`, `click_count`, `star_count`, `fork_count`,
     `last_commit_time`, `is_archived`, `last_sync_time`,
     `course_url`, `delete_flag`)
VALUES

-- 前端框架
('Vue',
 '渐进式 JavaScript 框架，用于构建用户界面。易上手、高性能，生态完善，国内使用率极高。',
 'https://github.com/vuejs/vue',
 'Evan You',
 'https://vuejs.org/images/logo.png',
 1, 0, 207000, 33600, '2024-09-01 00:00:00', 0, NOW(), NULL, 0),

('Nuxt',
 '基于 Vue 的全栈框架，支持 SSR / SSG / SPA，开箱即用的文件路由、自动导入等特性。',
 'https://github.com/nuxt/nuxt',
 'Nuxt Team',
 'https://nuxt.com/icon.png',
 1, 0, 55000, 5100, '2025-01-10 00:00:00', 0, NOW(), NULL, 0),

('React',
 'Meta 开源的 UI 库，组件化思想的先驱，全球使用最广泛的前端框架之一。',
 'https://github.com/facebook/react',
 'Meta',
 'https://reactjs.org/favicon.ico',
 1, 0, 229000, 47000, '2025-01-08 00:00:00', 0, NOW(), NULL, 0),

('Vite',
 '新一代前端构建工具，基于原生 ESM，冷启动极快，HMR 毫秒级响应，已成为前端工具链标配。',
 'https://github.com/vitejs/vite',
 'Evan You',
 'https://vitejs.dev/logo.svg',
 1, 0, 68000, 6200, '2025-01-09 00:00:00', 0, NOW(), NULL, 0),

('Tailwind CSS',
 '原子化 CSS 框架，通过组合工具类快速构建现代 UI，无需离开 HTML 即可完成样式开发。',
 'https://github.com/tailwindlabs/tailwindcss',
 'Tailwind Labs',
 'https://tailwindcss.com/favicons/favicon.ico',
 1, 0, 83000, 4200, '2025-01-07 00:00:00', 0, NOW(), NULL, 0),

-- 后端框架
('Spring Boot',
 'Spring 生态的快速开发框架，约定优于配置，极大简化了 Java 企业级应用的搭建与部署。',
 'https://github.com/spring-projects/spring-boot',
 'Spring Projects',
 'https://spring.io/favicon.ico',
 1, 0, 75000, 40800, '2025-01-10 00:00:00', 0, NOW(), NULL, 0),

('MyBatis-Plus',
 'MyBatis 的增强工具，在 MyBatis 基础上只做增强不做改变，提供强大的 CRUD、分页、代码生成等能力。',
 'https://github.com/baomidou/mybatis-plus',
 'baomidou',
 NULL,
 1, 0, 16500, 4400, '2025-01-05 00:00:00', 0, NOW(), NULL, 0),

('FastAPI',
 '基于 Python 的现代高性能 Web 框架，自动生成 OpenAPI 文档，类型提示友好，开发效率极高。',
 'https://github.com/fastapi/fastapi',
 'Sebastián Ramírez',
 'https://fastapi.tiangolo.com/img/favicon.png',
 1, 0, 78000, 6700, '2025-01-09 00:00:00', 0, NOW(), NULL, 0),

-- 数据库 / 中间件
('Redis',
 '高性能内存数据库，支持字符串、哈希、列表、集合等多种数据结构，广泛用于缓存、消息队列、分布式锁。',
 'https://github.com/redis/redis',
 'Redis Ltd.',
 NULL,
 1, 0, 67000, 23800, '2025-01-08 00:00:00', 0, NOW(), NULL, 0),

('Elasticsearch',
 '分布式搜索与分析引擎，基于 Lucene，支持全文检索、结构化查询、实时分析，ELK 栈核心组件。',
 'https://github.com/elastic/elasticsearch',
 'Elastic',
 NULL,
 1, 0, 70000, 25000, '2025-01-10 00:00:00', 0, NOW(), NULL, 0),

-- AI / 机器学习
('LangChain',
 '大语言模型应用开发框架，提供链式调用、记忆、工具调用、RAG 等能力，是 LLM 应用开发的事实标准。',
 'https://github.com/langchain-ai/langchain',
 'LangChain AI',
 NULL,
 1, 0, 96000, 15800, '2025-01-10 00:00:00', 0, NOW(), NULL, 0),

('Ollama',
 '在本地运行大语言模型的工具，支持 Llama、Mistral、Qwen 等主流模型，一行命令即可启动本地 AI。',
 'https://github.com/ollama/ollama',
 'Ollama',
 NULL,
 1, 0, 102000, 8200, '2025-01-10 00:00:00', 0, NOW(), NULL, 0),

-- 运维 / 云原生
('Docker',
 '容器化平台，将应用及其依赖打包为镜像，实现"一次构建，到处运行"，是现代 DevOps 的基础设施。',
 'https://github.com/docker/compose',
 'Docker',
 NULL,
 1, 0, 34000, 5600, '2025-01-09 00:00:00', 0, NOW(), NULL, 0),

('Kubernetes',
 '容器编排系统，自动化部署、扩缩容和管理容器化应用，云原生时代的操作系统。',
 'https://github.com/kubernetes/kubernetes',
 'CNCF',
 NULL,
 1, 0, 111000, 39800, '2025-01-10 00:00:00', 0, NOW(), NULL, 0),

-- 工具类
('Hutool',
 '国产 Java 工具库，封装了字符串、日期、加密、HTTP、Excel 等大量常用工具，减少重复代码。',
 'https://github.com/dromara/hutool',
 'dromara',
 NULL,
 1, 0, 30000, 7900, '2025-01-06 00:00:00', 0, NOW(), NULL, 0),

('Axios',
 '基于 Promise 的 HTTP 客户端，同时支持浏览器和 Node.js，是前端项目中使用最广泛的请求库。',
 'https://github.com/axios/axios',
 'axios',
 NULL,
 1, 0, 105000, 10900, '2025-01-07 00:00:00', 0, NOW(), NULL, 0);

-- 标签关联数据
-- 说明：project id 依赖上面 INSERT 的自增顺序，请根据实际 id 调整
-- 假设上面16条数据 id 从 1 开始（若表中已有数据请相应偏移）
-- 标签 id：1=Vue3, 2=Nuxt3, 3=Node.js, 4=Python, 5=AI实战, 6=Java, 7=React, 8=Go

-- Vue (id=1) → Vue3
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (1, 1, 0);
-- Nuxt (id=2) → Nuxt3, Vue3
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (2, 2, 0), (2, 1, 0);
-- React (id=3) → React
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (3, 7, 0);
-- Vite (id=4) → Vue3, Node.js
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (4, 1, 0), (4, 3, 0);
-- Tailwind CSS (id=5) → Node.js
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (5, 3, 0);
-- Spring Boot (id=6) → Java
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (6, 6, 0);
-- MyBatis-Plus (id=7) → Java
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (7, 6, 0);
-- FastAPI (id=8) → Python
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (8, 4, 0);
-- Redis (id=9) → 无对应标签，跳过
-- Elasticsearch (id=10) → Java
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (10, 6, 0);
-- LangChain (id=11) → Python, AI实战
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (11, 4, 0), (11, 5, 0);
-- Ollama (id=12) → AI实战, Go
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (12, 5, 0), (12, 8, 0);
-- Docker Compose (id=13) → Go
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (13, 8, 0);
-- Kubernetes (id=14) → Go
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (14, 8, 0);
-- Hutool (id=15) → Java
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (15, 6, 0);
-- Axios (id=16) → Node.js
INSERT INTO `osh_open_project_tag_rel` (`project_id`, `tag_id`, `delete_flag`) VALUES (16, 3, 0);
