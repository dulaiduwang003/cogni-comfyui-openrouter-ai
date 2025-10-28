export default {
  common: {
    confirm: '确定',
    cancel: '取消',
    save: '保存',
    delete: '删除',
    edit: '编辑',
    search: '搜索',
    reset: '重置',
    submit: '提交',
    back: '返回',
    retry: '重试',
    loading: '加载中...',
    success: '成功',
    error: '错误',
    warning: '警告',
    info: '信息',
    yes: '是',
    no: '否'
  },
  auth: {
    login: '登录',
    register: '注册',
    logout: '退出登录',
    loginNow: '立即登录',
    registerNow: '立即注册',
    forgotPassword: '忘记密码？',
    backToLogin: '返回登录',
    hasAccount: '已有账号？',
    noAccount: '没有账号？',
    
    // 登录表单
    passwordLogin: '密码登录',
    codeLogin: '验证码登录',
    account: '账号',
    email: '邮箱',
    password: '密码',
    verificationCode: '验证码',
    getCode: '获取验证码',
    sendingCode: '发送中...',
    resendCode: '重新发送',
    loginSuccess: '登录成功',
    registerSuccess: '注册成功，请登录',
    resetPasswordSuccess: '密码重置成功',
    
    // 表单验证
    pleaseEnterAccount: '请输入账号',
    pleaseEnterEmail: '请输入邮箱',
    pleaseEnterPassword: '请输入密码',
    pleaseEnterCode: '请输入验证码',
    pleaseEnterValidEmail: '请输入有效的邮箱地址',
    passwordMinLength: '密码长度至少6位',
    codeLength: '验证码长度为6位',
    
    // 注册
    confirmPassword: '确认密码',
    pleaseConfirmPassword: '请再次输入密码',
    passwordNotMatch: '两次输入的密码不一致',
    agreeTo: '我已阅读并同意',
    termsOfService: '服务条款',
    and: '和',
    privacyPolicy: '隐私政策',
    pleaseAgreeTerms: '请同意服务条款和隐私政策',
    
    // 重置密码
    resetPassword: '重置密码',
    newPassword: '新密码',
    pleaseEnterNewPassword: '请输入新密码'
  },
  navbar: {
    switchLanguage: '切换语言',
    switchTheme: '切换主题',
    myCredits: '我的积分',
    taskManagement: '任务管理',
    userMenu: '用户菜单',
    clickToLogin: '点击登录'
  },
  theme: {
    light: '浅色',
    dark: '深色',
    system: '跟随系统',
    darkRed: '深色 - 红色',
    lightSoftLavender: '浅色 - 薰衣草',
    lightRed: '浅色 - 红色'
  },
  authWrapper: {
    pleaseLogin: '请先登录',
    requireLogin: '此操作需要登录'
  },
  loading: {
    processing: '正在处理...'
  },
  markdown: {
    copySuccess: '代码已复制',
    copyFailed: '复制失败',
    copyCode: '复制代码',
    renderError: '内容渲染失败'
  },
  redemption: {
    title: '兑换码',
    code: '兑换码',
    enterCode: '请输入兑换码',
    pleaseEnterCode: '请输入兑换码',
    codeMinLength: '兑换码长度不能少于6位',
    redeem: '立即兑换',
    redeeming: '兑换中...',
    success: '兑换成功',
    successMessage: '恭喜您，兑换码使用成功！积分已发放到您的账户',
    tips: '兑换码使用说明：',
    tip1: '兑换码不区分大小写',
    tip2: '每个兑换码只能使用一次',
    tip3: '兑换成功后奖励将自动发放到您的账户'
  },
  task: {
    title: '任务',
    taskProgress: '任务进度',
    taskDescription: '任务将在创建后的24小时后自动删除',
    all: '全部',
    waiting: '等待中',
    building: '构建中',
    completed: '已完成',
    failed: '已失败',
    canceled: '已取消',
    loadingMore: '加载更多...',
    noMore: '没有更多任务了',
    noTasks: '暂无任务',
    waitingAt: '等待中,当前排在',
    joining: '加入中',
    viewWork: '查看作品',
    cancel: '取消任务',
    remake: '重新制作',
    remakeSuccess: '重新制作请求已发送，新任务将自动加入队列',
    cancelSuccess: '任务取消请求已发送',
    noWorkInfo: '该任务没有关联的作品信息',
    justNow: '刚刚',
    minutesAgo: '分钟前',
    hoursAgo: '小时前',
    daysAgo: '天前',
    taskId: 'ID',
    // WebSocket 通知
    taskCompletedWithWork: '已完成，生成{type}作品',
    taskCompleted: '已完成',
    taskFailed: '生成失败',
    taskCanceled: '已取消',
    taskStartBuilding: '开始构建',
    taskPrefix: '任务',
    serverError: '服务器错误',
    errorMessage: '错误: {message}'
  },
  workType: {
    image: '图片',
    video: '视频',
    audio: '音频',
    model: '模型',
    work: '作品'
  },
  user: {
    profileCenter: '个人中心',
    logout: '退出登录',
    defaultName: '用户'
  },
  workDetail: {
    title: '作品详情',
    workflowName: '工作流名称：',
    taskId: '任务ID：',
    workType: '作品类型：',
    createTime: '创建时间：',
    formParams: '生成参数：',
    noTips: '无提示信息',
    download: '下载作品',
    delete: '删除作品',
    deleteConfirm: '确定要删除这个作品吗？删除后将无法恢复。',
    deleteTitle: '确认删除',
    deleteButton: '确定删除',
    deleteSuccess: '作品删除成功',
    downloadFailed: '下载失败',
    downloadUnavailable: '下载链接不可用',
    deleteFailed: '删除失败',
    idUnavailable: '作品ID不可用',
    notFound: '找不到作品',
    notFoundMessage: '作品可能已经被删除',
    close: '关闭',
    audioLoadFailed: '音频加载失败',
    videoLoadFailed: '视频加载失败',
    imageLoadFailed: '图片加载失败',
    modelLoadFailed: '3D模型加载失败',
    audioTitle: '音频作品',
    unnamedWorkflow: '未命名工作流',
    paramImage: '参数图片',
    types: {
      textPrompt: '文本提示词',
      radioSelector: '单选选择器',
      checkboxSelector: '多选选择器',
      imageUpload: '图片上传',
      imageScribble: '图片涂鸦',
      videoUpload: '视频上传',
      audioUpload: '音频上传'
    }
  },

  // AI 对话相关
  chat: {
    // 侧边栏
    sidebar: {
      newChat: '新建主题',
      collapse: '折叠侧边栏',
      expand: '展开侧边栏',
      loading: '加载中…',
      selectModel: '选择模型',
      searchPlaceholder: 'Search rooms...',
      deleteSession: '删除会话',
      delete: '删除',
      menu: '菜单',
      newSession: '新会话',
      currentSessionLatest: '当前会话已经是最新的了'
    },
    
    // 欢迎页面
    welcome: {
      title: '有什么可以帮忙的？',
      description: '让思考，更有价值'
    },
    
    // 消息编辑器
    composer: {
      placeholder: '在此描述你的问题...',
      generateImage: '生成图片',
      webSearch: '联网搜索',
      clearAll: '清空',
      uploading: '上传中',
      suggestions: {
        creativeWriting: '创意写作',
        creativeWritingSubtitle: '帮我写一个有趣的故事',
        creativeWritingMessage: '请帮我写一个关于时间旅行的有趣故事',
        mathProblem: '数学问题',
        mathProblemSubtitle: '9.9 和 9.11 哪个更大？',
        mathProblemMessage: '请解释一下 9.9 和 9.11 哪个数字更大，并说明原因',
        wordGame: '文字游戏',
        wordGameSubtitle: '单词中有几个字母r？',
        wordGameMessage: '请告诉我"strawberry"这个单词中有几个字母r？',
        poetry: '诗歌创作',
        poetrySubtitle: '写一首12行的诗',
        poetryMessage: '请为我创作一首关于春天的12行诗歌',
        finance: '理财建议',
        financeSubtitle: '制定投资组合管理策略',
        financeMessage: '请帮我制定一个适合新手的投资组合管理策略',
        programming: '编程学习',
        programmingSubtitle: '如何开始学习编程？',
        programmingMessage: '我是编程新手，请推荐一个学习路径和适合的编程语言',
        health: '健康生活',
        healthSubtitle: '制定健康的作息计划',
        healthMessage: '请帮我制定一个健康的日常作息和运动计划',
        travel: '旅行规划',
        travelSubtitle: '推荐一个周末旅行目的地',
        travelMessage: '请推荐一个适合周末两天一夜的旅行目的地，包括行程安排',
        learning: '学习方法',
        learningSubtitle: '如何提高学习效率？',
        learningMessage: '请分享一些科学有效的学习方法和记忆技巧',
        career: '职场发展',
        careerSubtitle: '如何提升职业技能？',
        careerMessage: '请给我一些关于职业发展和技能提升的建议'
      }
    },
    
    // 聊天历史
    history: {
      empty: '暂无聊天记录',
      justNow: '刚刚',
      minutesAgo: '分钟前',
      hoursAgo: '小时前',
      daysAgo: '天前'
    },
    
    // 消息项
    message: {
      generating: '正在生成',
      typing: '正在输入',
      reasoningProcess: '思考过程',
      references: '参考来源',
      copyMessage: '复制消息',
      copySuccess: '复制成功',
      copyFailed: '复制失败',
      like: '点赞',
      dislike: '点踩',
      liked: '已点赞',
      disliked: '已点踩',
      regenerate: '重新生成',
      regenerating: '重新生成中...'
    },
    
    // 浮动按钮
    fab: {
      toggleSidebar: '折叠/展开侧边栏',
      clearSession: '删除当前会话记录',
      pinTop: '置顶',
      pinBottom: '置底',
      fullscreen: '全屏',
      exitFullscreen: '退出全屏'
    },
    
    // 模型选择
    model: {
      searchPlaceholder: '搜索模型名称或能力...',
      filter: '筛选',
      paymentType: '付费类型',
      all: '全部',
      free: '免费',
      paid: '付费',
      inputType: '输入类型',
      outputType: '输出类型',
      reasoningSupport: '推理支持',
      support: '支持',
      notSupport: '不支持',
      text: '文本',
      image: '图片',
      pdf: 'PDF',
      audio: '音频',
      file: '文件',
      webSearch: '联网搜索',
      imageGeneration: '生成图片',
      reasoning: '推理',
      noResults: '未找到匹配的模型',
      tryAdjustFilters: '尝试调整搜索条件或筛选器',
      foundModels: '找到 {total} 个匹配的模型，已显示 {shown} 个',
      applyFilters: '应用筛选',
      clearFilters: '清除筛选',
      loadingMore: '加载更多模型中...',
      allLoaded: '已加载所有模型',
      fetchFailed: '获取模型列表失败',
      capabilities: {
        textInput: '语言大模型',
        imageInput: '视觉',
        audioInput: '音频识别',
        fileInput: '文件识别',
        textOutput: '文本',
        imageOutput: '图像生成'
      }
    },
    
    // 错误提示
    error: {
      enterFullscreenFailed: '进入全屏失败',
      exitFullscreenFailed: '退出全屏失败',
      clearSessionFailed: '清空会话失败',
      deleteSessionFailed: '删除会话失败'
    }
  },
  
  // ComfyUI 工作流页面
  comfyui: {
    banner: {
      title: 'AI创意生成',
      description: 'AI 生成创意引擎：用可视化节点搭建灵感，风格混合、参数可调、批量生成，让好点子快速落地。'
    },
    search: {
      placeholder: '搜索',
      all: '全部'
    },
    empty: {
      title: '暂无工作流',
      description: '当前没有可用的工作流模板'
    },
    panel: {
      title: '工作流配置',
      category: '分类：',
      aiGenerated: '内容由AI生成',
      configPrompt: '请配置以下参数开始生成内容',
      submitSuccess: '任务提交成功',
      submitSuccessMessage: '任务已加入队列，您可以在右上角任务面板查看实时进度',
      submitFailed: '提交失败'
    },
    formRenderer: {
      loadFailed: '加载失败',
      loadWorkflowFailed: '加载工作流配置失败',
      generating: '生成中...',
      startGenerate: '开始生成',
      creditsDeduct: '消耗 ( {credits} 积分)',
      validation: {
        pleaseEnter: '请填写{field}',
        maxLength: '内容长度不能超过{max}个字符',
        thisField: '此项'
      }
    },
    imageScribble: {
      uploadTrigger: '上传图片以开始涂抹',
      uploadHint: '支持 JPG、PNG、GIF，≤{size}MB',
      reScribble: '重新涂抹',
      dialogTitle: '图片涂抹',
      uploadProgress: '{progress}%',
      draw: '涂抹',
      erase: '擦除',
      brushSize: '笔刷大小：',
      clear: '清空',
      cancel: '取消',
      confirm: '确认',
      uploading: '上传中...',
      processing: '正在处理图片...',
      messages: {
        fileSizeExceeded: '文件大小不能超过 {size}MB',
        onlyImageAllowed: '只能上传图片文件',
        uploadSuccess: '图片上传成功，现在可以开始涂抹',
        uploadFailed: '图片加载失败，请重试',
        canvasNotInitialized: '画布未初始化，请重新上传图片',
        scribbleComplete: '涂抹完成，图片已上传',
        processingFailed: '处理图片失败，请重试'
      }
    }
  },
  system: {
    tabs: {
      overview: '系统概览',
      users: '用户管理',
      workflow: '工作流管理',
      redemption: '兑换码管理',
      announcement: '站点公告'
    },
    overview: {
      title: '系统概况',
      updateTime: '更新时间',
      noData: '暂无数据',
      userStats: {
        title: '用户统计',
        totalUsers: '用户总数',
        onlineUsers: '在线用户',
        todayNewUsers: '今日新增'
      },
      aiStats: {
        title: 'AI 服务统计',
        todayApiCalls: '今日调用',
        todayTokens: '今日Token',
        todayConversations: '今日对话',
        activeModelsTop: '活跃模型 TOP'
      },
      taskStats: {
        title: '任务状态',
        queuedTasks: '排队中',
        buildingTasks: '构建中'
      },
      workflowStats: {
        title: '工作流统计',
        totalWorkflows: '工作流总数',
        todayTasks: '今日任务',
        todaySuccessTasks: '成功任务',
        todayFailedTasks: '失败任务'
      },
      systemResources: {
        title: '系统资源',
        cpuUsage: 'CPU 使用率',
        memoryUsage: '内存使用率'
      },
      errors: {
        fetchFailed: '获取系统概况失败'
      }
    },
    users: {
      title: '用户管理',
      createUser: '新增用户',
      editUser: '编辑用户',
      searchPlaceholder: '搜索邮箱或昵称',
      userRole: '用户角色',
      search: '搜索',
      reset: '重置',
      table: {
        id: 'ID',
        avatar: '头像',
        email: '邮箱',
        nickname: '昵称',
        role: '角色',
        createTime: '创建时间',
        updateTime: '更新时间',
        actions: '操作',
        edit: '编辑',
        delete: '删除'
      },
      roles: {
        user: '普通用户',
        admin: '管理员'
      },
      form: {
        email: '邮箱',
        password: '密码',
        nickname: '昵称',
        avatar: '头像',
        role: '角色',
        emailPlaceholder: '请输入邮箱',
        passwordPlaceholder: '请输入密码',
        nicknamePlaceholder: '请输入昵称',
        rolePlaceholder: '请选择角色',
        uploadAvatar: '上传头像',
        removeAvatar: '移除',
        changeAvatar: '更换',
        avatarHint: '建议 1:1 比例，支持 JPG/PNG，大小 ≤ 2MB'
      },
      validation: {
        emailRequired: '请输入邮箱',
        emailFormat: '请输入正确的邮箱格式',
        passwordRequired: '请输入密码',
        passwordMinLength: '密码长度不能少于6位',
        nicknameRequired: '请输入昵称',
        nicknameMaxLength: '昵称长度不能超过50个字符',
        roleRequired: '请选择角色',
        avatarFormatError: '仅支持 JPG、JPEG、PNG 格式',
        avatarSizeError: '图片大小不能超过 2MB'
      },
      messages: {
        deleteConfirm: '确定要删除用户 "{name}" 吗？',
        deleteTitle: '删除确认',
        deleteSuccess: '删除成功',
        createSuccess: '创建成功',
        updateSuccess: '更新成功',
        avatarUploadSuccess: '头像上传成功'
      }
    },
    actionPanel: {
      title: '操作栏',
      userManagement: '用户管理',
      userManagementDesc: '管理系统用户',
      workflowManagement: '工作流管理',
      workflowManagementDesc: '配置和管理工作流',
      redemptionManagement: '兑换码管理',
      redemptionManagementDesc: '生成和管理兑换码',
      announcement: '站点公告',
      announcementDesc: '发布和管理站点公告',
      clickedAction: '点击了 {action}'
    },
    redemption: {
      title: '兑换码管理',
      create: '新建兑换码',
      search: {
        keyword: '输入兑换码关键字',
        status: '状态',
        query: '查询',
        reset: '重置'
      },
      table: {
        id: 'ID',
        code: '兑换码',
        credits: '积分',
        status: '状态',
        usedBy: '使用者ID',
        usedTime: '使用时间',
        expireTime: '过期时间',
        description: '描述',
        createTime: '创建时间',
        actions: '操作',
        edit: '编辑',
        delete: '删除'
      },
      status: {
        valid: '有效',
        used: '已使用',
        disabled: '已禁用'
      },
      dialog: {
        createTitle: '新建兑换码',
        editTitle: '编辑兑换码',
        credits: '积分',
        prefix: '前缀',
        prefixPlaceholder: '可选，如 VIP-',
        length: '长度',
        expireTime: '过期时间',
        expireTimePlaceholder: '选择过期时间（可选)',
        description: '描述',
        descriptionPlaceholder: '填写描述信息（可选）',
        cancel: '取消',
        createBtn: '创建',
        saveBtn: '保存',
        codeLabel: '兑换码',
        statusLabel: '状态',
        statusPlaceholder: '选择状态',
        usedNotEditable: '已使用，不可修改'
      },
      validation: {
        creditsRequired: '请输入积分',
        creditsMustBeNumber: '积分必须为数字',
        creditsNotNegative: '积分不能小于0',
        prefixMaxLength: '前缀不超过16字符',
        lengthRequired: '请输入长度',
        lengthNotEmpty: '长度不能为空',
        lengthMin: '长度至少为 4',
        lengthMax: '长度不能超过 64',
        descriptionMaxLength: '描述不超过200字',
        statusRequired: '请选择状态'
      },
      messages: {
        loadFailed: '加载失败',
        deleteConfirm: '确定删除该兑换码？',
        deleteSuccess: '删除成功',
        deleteFailed: '删除失败',
        createSuccess: '创建成功',
        createFailed: '创建失败',
        saveSuccess: '保存成功',
        saveFailed: '保存失败'
      }
    },
    workflow: {
      title: '工作流管理',
      create: '新建工作流',
      categoryManagement: '类别管理',
      search: {
        keyword: '搜索工作流名称',
        category: '选择类别',
        allCategories: '全部类别',
        query: '搜索',
        reset: '重置'
      },
      table: {
        cover: '封面',
        id: 'ID',
        name: '名称',
        description: '描述',
        category: '类别',
        credits: '扣除积分',
        actions: '操作',
        edit: '编辑',
        delete: '删除'
      },
      dialog: {
        createTitle: '新建工作流',
        editTitle: '编辑工作流',
        categoryTitle: '类别管理',
        createCategoryTitle: '新增类别',
        basicInfo: '基本信息',
        name: '名称',
        namePlaceholder: '请输入工作流名称',
        cover: '封面图',
        coverPlaceholder: '输入图片URL或上传',
        upload: '上传',
        description: '描述',
        descriptionPlaceholder: '可选：工作流描述',
        category: '类别',
        categoryPlaceholder: '选择类别',
        credits: '扣除积分',
        parseWorkflow: '上传并解析工作流 JSON',
        chooseFile: '选择文件',
        parsed: '已解析',
        parseSuccess: '解析成功',
        parseSummary: '识别到可配置输入节点：{formNodes}，全部节点：{allNodes}',
        inputConfig: '输入节点配置',
        outputConfig: '输出节点配置',
        addOutput: '添加输出',
        removeOutput: '移除',
        selectNode: '选择节点',
        selectType: '选择类型',
        nodeLabel: '{tips}（{key}）',
        unnamedNode: '未命名节点',
        enableForm: '设置表单组件',
        required: '必填',
        optional: '可选',
        formLabel: '表单标签/提示',
        defaultTemplate: '默认值模板（可选）',
        sizeLength: '大小/长度',
        options: '选项',
        optionsPlaceholder: '选项（JSON 对象字符串，如 {"low":"低","mid":"中"}）',
        inputField: '输入字段',
        cancel: '取消',
        save: '保存',
        categoryName: '类别名称',
        categoryNamePlaceholder: '请输入类别名称',
        createCategory: '新增类别',
        confirm: '确定',
        coverUploadSuccess: '封面上传成功'
      },
      formTypes: {
        TEXT_PROMPT: '文本输入',
        RADIO_SELECTOR: '单选',
        CHECKBOX_SELECTOR: '多选',
        IMAGE_UPLOAD: '图片上传',
        IMAGE_SCRIBBLE: '图片涂抹',
        IMAGE_CONFIGURABLE: '图片类（需选择具体控件）',
        VIDEO_UPLOAD: '视频上传',
        AUDIO_UPLOAD: '音频上传',
        TEXT_CONFIGURABLE: '文本类（需选择具体控件）'
      },
      outputTypes: {
        image: '图片',
        video: '视频',
        audio: '音频'
      },
      validation: {
        nameRequired: '请输入工作流名称',
        nameLength: '名称长度在 1 到 100 个字符',
        categoryRequired: '请选择类别',
        creditsRequired: '请输入扣除积分',
        creditsMin: '积分不能小于 0',
        categoryNameRequired: '请输入类别名称',
        categoryNameLength: '长度在 1 到 50 个字符',
        fileFormatError: '请上传 .json 文件',
        optionsRequired: '选择器类型必须提供 options',
        optionsInvalidFormat: 'options 必须为非空 JSON 对象',
        optionsJsonError: 'options 必须为 JSON 对象字符串'
      },
      messages: {
        deleteConfirm: '确认删除"{name}"？该操作不会删除已生成的作品，仅删除表单与输出配置。',
        deleteTitle: '删除确认',
        deleteSuccess: '删除成功',
        updateSuccess: '更新成功',
        saveSuccess: '工作流配置保存成功',
        categoryCreateSuccess: '类别创建成功',
        categorySaveSuccess: '保存成功',
        categoryDeleteConfirm: '确认删除类别"{name}"？',
        categoryDeleteSuccess: '删除成功',
        cancel: '取消'
      }
    },
    announcement: {
      title: '站点公告',
      publishButton: '发布公告',
      clearButton: '清空公告',
      dialogTitle: '发布公告',
      publisher: '发布人',
      publisherLabel: '发布人',
      publisherPlaceholder: '请输入发布人',
      form: {
        title: '标题',
        titlePlaceholder: '请输入公告标题',
        publisher: '发布人',
        publisherPlaceholder: '请输入发布人',
        content: '内容',
        contentPlaceholder: '请输入公告内容',
        time: '时间（可选）',
        timePlaceholder: '选择发布时间（不选则使用当前时间）'
      },
      validation: {
        titleRequired: '请输入标题',
        titleMaxLength: '标题不超过100字',
        publisherRequired: '请输入发布人',
        publisherMaxLength: '发布人不超过50字',
        contentRequired: '请输入内容',
        contentMaxLength: '内容不超过1000字'
      },
      messages: {
        publishSuccess: '发布成功',
        clearConfirm: '确定清空当前站点公告吗？',
        clearTitle: '清空确认',
        clearSuccess: '已清空公告'
      },
      empty: {
        title: '暂无站点公告',
        hint: '点击右上角"发布公告"进行发布'
      }
    }
  },
  profile: {
    dialogs: {
      changeAvatar: '更换头像',
      editNickname: '编辑昵称'
    },
    banner: {
      avatarAlt: '用户头像',
      changeAvatar: '更换头像',
      defaultNickname: '未设置昵称',
      edit: '编辑',
      availableCredits: '可用积分',
      frozenCredits: '冻结积分',
      totalCredits: '总积分'
    },
    transactions: {
      title: '积分流水',
      refresh: '刷新',
      selectType: '选择交易类型',
      allTransactions: '全部交易',
      recharge: '充值积分',
      consume: '消费积分',
      refund: '退还积分',
      freeze: '冻结积分',
      loading: '加载中...',
      noRecords: '暂无积分流水记录',
      allLoaded: '已显示全部记录',
      fetchError: '获取积分流水失败'
    },
    avatar: {
      selectImage: '点击选择头像图片',
      formatHint: '支持 JPG、PNG 格式，文件大小不超过 2MB',
      preview: '预览图片',
      cancel: '取消',
      uploading: '上传中...',
      upload: '确认上传',
      formatError: '请选择 JPG 或 PNG 格式的图片',
      sizeError: '图片大小不能超过 2MB',
      invalidFile: '请选择有效的图片文件',
      uploadSuccess: '您的头像已成功更新！',
      uploadFailed: '头像上传失败'
    },
    nickname: {
      currentLabel: '当前昵称',
      notSet: '未设置',
      newLabel: '新昵称',
      placeholder: '请输入新昵称',
      rulesTitle: '昵称规则',
      rule1: '长度为 2-20 个字符',
      rule2: '可以包含中文、英文、数字',
      rule3: '不能包含特殊符号',
      rule4: '不能为纯数字',
      cancel: '取消',
      saving: '保存中...',
      save: '保存',
      required: '请输入昵称',
      lengthError: '昵称长度在 2 到 20 个字符',
      formatError: '昵称只能包含中文、英文、数字，且不能为纯数字',
      sameError: '新昵称不能与当前昵称相同',
      invalidError: '请输入有效的昵称',
      updateSuccess: '昵称更新成功',
      updateFailed: '昵称更新失败'
    }
  },
  works: {
    title: '我的作品',
    noMore: '已显示全部作品',
    fetchError: '获取作品列表失败',
    taskCompleted: '检测到任务完成，刷新作品列表',
    batchSelect: '选择',
    selectAll: '全选',
    selectedCount: '已选择 {count} 项',
    batchDelete: '批量删除',
    cancel: '取消',
    noWorksSelected: '请先选择要删除的作品',
    batchDeleteConfirm: '确定要删除选中的 {count} 个作品吗？删除后将无法恢复。',
    batchDeleteTitle: '批量删除确认',
    confirmDelete: '确认删除',
    batchDeleteSuccess: '成功删除了 {count} 个作品',
    batchDeleteFailed: '批量删除失败，请重试',
    banner: {
      title: '我的创作作品集',
      description: '探索 AI 创意的无限可能，每一件作品都是灵感与技术的完美结合',
      stats: {
        aiCreation: 'AI 创作',
        highQuality: '高质量',
        permanentSave: '永久保存'
      }
    },
    empty: {
      title: '还没有作品',
      description: '开始创建您的第一个作品吧',
      createButton: '创建作品'
    },
    model3D: {
      noModel: '暂无3D模型',
      loading: '加载3D模型中...',
      alt: '3D模型'
    },
    workCard: {
      videoNotSupported: '您的浏览器不支持视频播放',
      audioNoPreview: '音频文件不支持预览',
      loadFailed: '作品加载失败',
      workPrefix: '作品',
      type: {
        image: '图片',
        video: '视频',
        audio: '音频',
        model: '模型'
      },
      time: {
        justNow: '刚刚',
        minutesAgo: '分钟前',
        hoursAgo: '小时前'
      }
    }
  },
  
  // 工具函数
  utils: {
    // 时间分组
    timeGroup: {
      today: '今天',
      yesterday: '昨天',
      thisWeek: '本周',
      lastWeek: '上周',
      thisMonth: '本月',
      lastMonth: '上月',
      earlier: '更早'
    },
    
    // 网络请求错误
    request: {
      networkDisconnected: '当前网络已断开，请检查本地网络连接',
      networkRequestFailed: '网络请求失败，请检查本地网络连接',
      unauthorizedWarning: '登录信息失效，请重新登录',
      requestFailed: '请求失败',
      requestError: '请求发生错误，请稍后重试',
      badRequest: '请求参数错误',
      unauthorized: '登录信息失效，请重新登录',
      forbidden: '访问被拒绝',
      notFound: '请求的资源不存在',
      internalServerError: '服务器内部错误',
      serverResponseFailed: '服务器响应失败',
      serverNoResponse: '服务器无响应，请检查本地网络连接',
      networkConnectionFailed: '网络连接失败，请检查本地网络连接',
      requestTimeout: '请求超时，请检查本地网络后再试'
    }
  },

  layouts: {
    // 侧边栏菜单
    sidebar: {
      menu: {
        onlineGeneration: '在线生成',
        onlineGenerationDesc: 'ComfyUI 工作流',
        myWorks: '我的作品',
        profile: '个人中心',
        aiChat: 'AI聊天',
        aiChatBadge: '免费',
        redemptionCode: '兑换码',
        systemManagement: '系统管理',
        sectionAI: 'AI',
        sectionOther: '其他'
      },
      footer: {
        announcement: '查看公告',
        visitGitHub: '访问 GitHub',
        aboutUs: '关于我们'
      }
    },
    // 提示文本
    authRequired: {
      redemptionCode: '使用兑换码功能需要先登录'
    }
  },
  
  about: {
    title: '关于我们',
    hero: {
      title: 'Conni-X-Pro',
      subtitle: 'AI 创意生成平台',
      description: '探索人工智能的无限可能，让创意触手可及'
    },
    project: {
      title: '项目介绍',
      description: 'Conni-X-Pro 是一个强大的 AI 创意生成平台，集成了 ComfyUI 工作流、多模型 AI 对话等功能。我们致力于为用户提供简单易用、功能强大的 AI 创作工具。',
      features: {
        title: '核心功能',
        items: {
          workflow: {
            title: 'ComfyUI 工作流',
            desc: '强大的可视化节点编辑器，支持图片、视频、音频、3D模型生成'
          },
          aiChat: {
            title: '多模型 AI 对话',
            desc: '集成多种主流 AI 模型，支持文本、图片、文件等多模态输入'
          },
          management: {
            title: '作品管理',
            desc: '永久保存你的创作，随时查看和管理你的 AI 作品集'
          },
          system: {
            title: '系统管理',
            desc: '完善的用户管理、工作流管理、积分系统等后台功能'
          }
        }
      },
      tech: {
        title: '技术栈',
        frontend: 'Vue 3 + TypeScript + Vite',
        ui: 'Element Plus + 自定义主题',
        backend: 'Node.js + Express',
        ai: 'ComfyUI + OpenAI Compatible API'
      }
    },
    team: {
      title: '团队介绍',
      members: {
        developer: {
          name: '开发者',
          role: '全栈工程师',
          desc: '热爱技术，专注于 AI 应用开发'
        }
      },
      contribute: '欢迎加入我们，一起构建更好的 AI 创作平台！'
    },
    sponsor: {
      title: '支持我们',
      description: '如果你觉得这个项目对你有帮助，欢迎赞助支持我们的开发工作',
      wechat: '微信赞赏',
      alipay: '支付宝',
      thanks: '感谢你的支持！'
    },
    links: {
      github: 'GitHub 仓库',
      documentation: '使用文档',
      contact: '联系我们'
    },
    footer: {
      license: '开源协议',
      version: '版本'
    }
  }
}

