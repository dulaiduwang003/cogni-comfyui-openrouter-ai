export default {
  common: {
    confirm: 'Confirm',
    cancel: 'Cancel',
    save: 'Save',
    delete: 'Delete',
    edit: 'Edit',
    search: 'Search',
    reset: 'Reset',
    submit: 'Submit',
    back: 'Back',
    retry: 'Retry',
    loading: 'Loading...',
    success: 'Success',
    error: 'Error',
    warning: 'Warning',
    info: 'Info',
    yes: 'Yes',
    no: 'No'
  },
  auth: {
    login: 'Login',
    register: 'Register',
    logout: 'Logout',
    loginNow: 'Login Now',
    registerNow: 'Register Now',
    forgotPassword: 'Forgot Password?',
    backToLogin: 'Back to Login',
    hasAccount: 'Already have an account?',
    noAccount: "Don't have an account?",
    
    // Login Form
    passwordLogin: 'Password Login',
    codeLogin: 'Verification Code Login',
    account: 'Account',
    email: 'Email',
    password: 'Password',
    verificationCode: 'Verification Code',
    getCode: 'Get Code',
    sendingCode: 'Sending...',
    resendCode: 'Resend',
    loginSuccess: 'Login successful',
    registerSuccess: 'Registration successful, please login',
    resetPasswordSuccess: 'Password reset successful',
    
    // Form Validation
    pleaseEnterAccount: 'Please enter account',
    pleaseEnterEmail: 'Please enter email',
    pleaseEnterPassword: 'Please enter password',
    pleaseEnterCode: 'Please enter verification code',
    pleaseEnterValidEmail: 'Please enter a valid email address',
    passwordMinLength: 'Password must be at least 6 characters',
    codeLength: 'Verification code must be 6 characters',
    
    // Register
    confirmPassword: 'Confirm Password',
    pleaseConfirmPassword: 'Please enter password again',
    passwordNotMatch: 'Passwords do not match',
    agreeTo: 'I have read and agree to',
    termsOfService: 'Terms of Service',
    and: 'and',
    privacyPolicy: 'Privacy Policy',
    pleaseAgreeTerms: 'Please agree to the Terms of Service and Privacy Policy',
    
    // Reset Password
    resetPassword: 'Reset Password',
    newPassword: 'New Password',
    pleaseEnterNewPassword: 'Please enter new password'
  },
  navbar: {
    switchLanguage: 'Switch Language',
    switchTheme: 'Switch Theme',
    myCredits: 'My Credits',
    taskManagement: 'Task Management',
    userMenu: 'User Menu',
    clickToLogin: 'Click to Login'
  },
  theme: {
    light: 'Light',
    dark: 'Dark',
    system: 'System',
    darkRed: 'Dark - Red',
    lightSoftLavender: 'Light - Soft Lavender',
    lightRed: 'Light - Red'
  },
  authWrapper: {
    pleaseLogin: 'Please login first',
    requireLogin: 'This action requires login'
  },
  loading: {
    processing: 'Processing...'
  },
  markdown: {
    copySuccess: 'Code copied',
    copyFailed: 'Copy failed',
    copyCode: 'Copy',
    renderError: 'Content render failed'
  },
  redemption: {
    title: 'Redemption Code',
    code: 'Code',
    enterCode: 'Please enter redemption code',
    pleaseEnterCode: 'Please enter redemption code',
    codeMinLength: 'Code must be at least 6 characters',
    redeem: 'Redeem Now',
    redeeming: 'Redeeming...',
    success: 'Redemption Successful',
    successMessage: 'Congratulations! Credits have been added to your account',
    tips: 'Redemption Code Instructions:',
    tip1: 'Code is case-insensitive',
    tip2: 'Each code can only be used once',
    tip3: 'Rewards will be automatically added to your account'
  },
  task: {
    title: 'Tasks',
    taskProgress: 'Task Progress',
    taskDescription: 'Tasks will be automatically deleted 24 hours after creation',
    all: 'All',
    waiting: 'Waiting',
    building: 'Building',
    completed: 'Completed',
    failed: 'Failed',
    canceled: 'Canceled',
    loadingMore: 'Loading more...',
    noMore: 'No more tasks',
    noTasks: 'No tasks yet',
    waitingAt: 'Waiting, currently at',
    joining: 'Joining',
    viewWork: 'View Work',
    cancel: 'Cancel Task',
    remake: 'Remake',
    remakeSuccess: 'Remake request sent, new task will be added to queue',
    cancelSuccess: 'Cancel request sent',
    noWorkInfo: 'No work information associated with this task',
    justNow: 'Just now',
    minutesAgo: 'minutes ago',
    hoursAgo: 'hours ago',
    daysAgo: 'days ago',
    taskId: 'ID',
    // WebSocket Notifications
    taskCompletedWithWork: 'completed, generated {type} work',
    taskCompleted: 'completed',
    taskFailed: 'generation failed',
    taskCanceled: 'canceled',
    taskStartBuilding: 'started building',
    taskPrefix: 'Task',
    serverError: 'Server Error',
    errorMessage: 'Error: {message}'
  },
  workType: {
    image: 'Image',
    video: 'Video',
    audio: 'Audio',
    model: 'Model',
    work: 'Work'
  },
  user: {
    profileCenter: 'Profile Center',
    logout: 'Logout',
    defaultName: 'User'
  },
  workDetail: {
    title: 'Work Details',
    workflowName: 'Workflow Name:',
    taskId: 'Task ID:',
    workType: 'Work Type:',
    createTime: 'Created:',
    formParams: 'Generation Parameters:',
    noTips: 'No tips',
    download: 'Download',
    delete: 'Delete',
    deleteConfirm: 'Are you sure you want to delete this work? This action cannot be undone.',
    deleteTitle: 'Confirm Delete',
    deleteButton: 'Delete',
    deleteSuccess: 'Work deleted successfully',
    downloadFailed: 'Download failed',
    downloadUnavailable: 'Download link unavailable',
    deleteFailed: 'Delete failed',
    idUnavailable: 'Work ID unavailable',
    notFound: 'Work not found',
    notFoundMessage: 'The work may have been deleted',
    close: 'Close',
    audioLoadFailed: 'Audio loading failed',
    videoLoadFailed: 'Video loading failed',
    imageLoadFailed: 'Image loading failed',
    modelLoadFailed: '3D model loading failed',
    audioTitle: 'Audio Work',
    unnamedWorkflow: 'Unnamed Workflow',
    paramImage: 'Parameter Image',
    types: {
      textPrompt: 'Text Prompt',
      radioSelector: 'Radio Selector',
      checkboxSelector: 'Checkbox Selector',
      imageUpload: 'Image Upload',
      imageScribble: 'Image Scribble',
      videoUpload: 'Video Upload',
      audioUpload: 'Audio Upload'
    }
  },

  // AI Chat
  chat: {
    // Sidebar
    sidebar: {
      newChat: 'New Chat',
      collapse: 'Collapse sidebar',
      expand: 'Expand sidebar',
      loading: 'Loading…',
      selectModel: 'Select Model',
      searchPlaceholder: 'Search rooms...',
      deleteSession: 'Delete session',
      delete: 'Delete',
      menu: 'Menu',
      newSession: 'New Session',
      currentSessionLatest: 'Current session is already the latest'
    },
    
    // Welcome page
    welcome: {
      title: 'How can I help you?',
      description: 'Make thinking more valuable'
    },
    
    // Message composer
    composer: {
      placeholder: 'Describe your question here...',
      generateImage: 'Generate Image',
      webSearch: 'Web Search',
      clearAll: 'Clear All',
      uploading: 'Uploading',
      suggestions: {
        creativeWriting: 'Creative Writing',
        creativeWritingSubtitle: 'Write an interesting story',
        creativeWritingMessage: 'Please write an interesting story about time travel',
        mathProblem: 'Math Problem',
        mathProblemSubtitle: 'Which is larger, 9.9 or 9.11?',
        mathProblemMessage: 'Please explain which number is larger, 9.9 or 9.11, and why',
        wordGame: 'Word Game',
        wordGameSubtitle: 'How many letter r in the word?',
        wordGameMessage: 'Please tell me how many letter r are in the word "strawberry"?',
        poetry: 'Poetry',
        poetrySubtitle: 'Write a 12-line poem',
        poetryMessage: 'Please create a 12-line poem about spring',
        finance: 'Financial Advice',
        financeSubtitle: 'Create investment portfolio strategy',
        financeMessage: 'Please help me create an investment portfolio management strategy for beginners',
        programming: 'Programming',
        programmingSubtitle: 'How to start learning programming?',
        programmingMessage: "I'm a programming beginner, please recommend a learning path and suitable programming language",
        health: 'Healthy Living',
        healthSubtitle: 'Create a healthy schedule',
        healthMessage: 'Please help me create a healthy daily routine and exercise plan',
        travel: 'Travel Planning',
        travelSubtitle: 'Recommend a weekend destination',
        travelMessage: 'Please recommend a suitable weekend two-day trip destination with itinerary',
        learning: 'Learning Methods',
        learningSubtitle: 'How to improve learning efficiency?',
        learningMessage: 'Please share some scientifically effective learning methods and memory techniques',
        career: 'Career Development',
        careerSubtitle: 'How to improve professional skills?',
        careerMessage: 'Please give me some advice on career development and skill improvement'
      }
    },
    
    // Chat history
    history: {
      empty: 'No chat history',
      justNow: 'Just now',
      minutesAgo: ' minutes ago',
      hoursAgo: ' hours ago',
      daysAgo: ' days ago'
    },
    
    // Message item
    message: {
      generating: 'Generating',
      typing: 'Typing',
      reasoningProcess: 'Reasoning Process',
      references: 'References',
      copyMessage: 'Copy message',
      copySuccess: 'Copied successfully',
      copyFailed: 'Copy failed',
      like: 'Like',
      dislike: 'Dislike',
      liked: 'Liked',
      disliked: 'Disliked',
      regenerate: 'Regenerate',
      regenerating: 'Regenerating...'
    },
    
    // Floating action buttons
    fab: {
      toggleSidebar: 'Toggle sidebar',
      clearSession: 'Clear current session',
      pinTop: 'Pin to top',
      pinBottom: 'Pin to bottom',
      fullscreen: 'Fullscreen',
      exitFullscreen: 'Exit fullscreen'
    },
    
    // Model selection
    model: {
      searchPlaceholder: 'Search model name or capabilities...',
      filter: 'Filter',
      paymentType: 'Payment Type',
      all: 'All',
      free: 'Free',
      paid: 'Paid',
      inputType: 'Input Type',
      outputType: 'Output Type',
      reasoningSupport: 'Reasoning Support',
      support: 'Support',
      notSupport: 'Not Support',
      text: 'Text',
      image: 'Image',
      pdf: 'PDF',
      audio: 'Audio',
      file: 'File',
      webSearch: 'Web Search',
      imageGeneration: 'Image Generation',
      reasoning: 'Reasoning',
      noResults: 'No matching models found',
      tryAdjustFilters: 'Try adjusting search terms or filters',
      foundModels: 'Found {total} matching models, showing {shown}',
      applyFilters: 'Apply Filters',
      clearFilters: 'Clear Filters',
      loadingMore: 'Loading more models...',
      allLoaded: 'All models loaded',
      fetchFailed: 'Failed to fetch model list',
      capabilities: {
        textInput: 'Language Model',
        imageInput: 'Vision',
        audioInput: 'Audio Recognition',
        fileInput: 'File Recognition',
        textOutput: 'Text',
        imageOutput: 'Image Generation'
      }
    },
    
    // Error messages
    error: {
      enterFullscreenFailed: 'Failed to enter fullscreen',
      exitFullscreenFailed: 'Failed to exit fullscreen',
      clearSessionFailed: 'Failed to clear session',
      deleteSessionFailed: 'Failed to delete session'
    }
  },
  
  // ComfyUI Workflow Page
  comfyui: {
    banner: {
      title: 'AI Creative Generation',
      description: 'AI-powered creative engine: Build inspiration with visual nodes, mix styles, adjust parameters, batch generate, and bring ideas to life quickly.'
    },
    search: {
      placeholder: 'Search',
      all: 'All'
    },
    empty: {
      title: 'No Workflows',
      description: 'No workflow templates available'
    },
    panel: {
      title: 'Workflow Configuration',
      category: 'Category:',
      aiGenerated: 'AI Generated Content',
      configPrompt: 'Please configure the following parameters to start generating content',
      submitSuccess: 'Task Submitted Successfully',
      submitSuccessMessage: 'Task has been added to the queue. You can view real-time progress in the task panel at the top right corner',
      submitFailed: 'Submit Failed'
    },
    formRenderer: {
      loadFailed: 'Load Failed',
      loadWorkflowFailed: 'Failed to load workflow configuration',
      generating: 'Generating...',
      startGenerate: 'Start Generate',
      creditsDeduct: 'Cost ( {credits} Credits)',
      validation: {
        pleaseEnter: 'Please enter {field}',
        maxLength: 'Content length cannot exceed {max} characters',
        thisField: 'this field'
      }
    },
    imageScribble: {
      uploadTrigger: 'Upload image to start scribbling',
      uploadHint: 'Supports JPG, PNG, GIF, ≤{size}MB',
      reScribble: 'Re-scribble',
      dialogTitle: 'Image Scribble',
      uploadProgress: '{progress}%',
      draw: 'Draw',
      erase: 'Erase',
      brushSize: 'Brush Size:',
      clear: 'Clear',
      cancel: 'Cancel',
      confirm: 'Confirm',
      uploading: 'Uploading...',
      processing: 'Processing image...',
      messages: {
        fileSizeExceeded: 'File size cannot exceed {size}MB',
        onlyImageAllowed: 'Only image files are allowed',
        uploadSuccess: 'Image uploaded successfully, you can start scribbling now',
        uploadFailed: 'Image loading failed, please try again',
        canvasNotInitialized: 'Canvas not initialized, please re-upload the image',
        scribbleComplete: 'Scribble complete, image uploaded',
        processingFailed: 'Image processing failed, please try again'
      }
    }
  },
  system: {
    tabs: {
      overview: 'System Overview',
      users: 'User Management',
      workflow: 'Workflow Management',
      redemption: 'Redemption Code',
      announcement: 'Site Announcement'
    },
    overview: {
      title: 'System Overview',
      updateTime: 'Update Time',
      noData: 'No Data',
      userStats: {
        title: 'User Statistics',
        totalUsers: 'Total Users',
        onlineUsers: 'Online Users',
        todayNewUsers: 'New Today'
      },
      aiStats: {
        title: 'AI Service Statistics',
        todayApiCalls: 'API Calls Today',
        todayTokens: 'Tokens Today',
        todayConversations: 'Conversations Today',
        activeModelsTop: 'Active Models TOP'
      },
      taskStats: {
        title: 'Task Status',
        queuedTasks: 'Queued',
        buildingTasks: 'Building'
      },
      workflowStats: {
        title: 'Workflow Statistics',
        totalWorkflows: 'Total Workflows',
        todayTasks: 'Tasks Today',
        todaySuccessTasks: 'Success Tasks',
        todayFailedTasks: 'Failed Tasks'
      },
      systemResources: {
        title: 'System Resources',
        cpuUsage: 'CPU Usage',
        memoryUsage: 'Memory Usage'
      },
      errors: {
        fetchFailed: 'Failed to fetch system overview'
      }
    },
    users: {
      title: 'User Management',
      createUser: 'Create User',
      editUser: 'Edit User',
      searchPlaceholder: 'Search by email or nickname',
      userRole: 'User Role',
      search: 'Search',
      reset: 'Reset',
      table: {
        id: 'ID',
        avatar: 'Avatar',
        email: 'Email',
        nickname: 'Nickname',
        role: 'Role',
        createTime: 'Create Time',
        updateTime: 'Update Time',
        actions: 'Actions',
        edit: 'Edit',
        delete: 'Delete'
      },
      roles: {
        user: 'User',
        admin: 'Admin'
      },
      form: {
        email: 'Email',
        password: 'Password',
        nickname: 'Nickname',
        avatar: 'Avatar',
        role: 'Role',
        emailPlaceholder: 'Please enter email',
        passwordPlaceholder: 'Please enter password',
        nicknamePlaceholder: 'Please enter nickname',
        rolePlaceholder: 'Please select role',
        uploadAvatar: 'Upload Avatar',
        removeAvatar: 'Remove',
        changeAvatar: 'Change',
        avatarHint: 'Recommended 1:1 ratio, JPG/PNG supported, size ≤ 2MB'
      },
      validation: {
        emailRequired: 'Please enter email',
        emailFormat: 'Please enter a valid email',
        passwordRequired: 'Please enter password',
        passwordMinLength: 'Password must be at least 6 characters',
        nicknameRequired: 'Please enter nickname',
        nicknameMaxLength: 'Nickname cannot exceed 50 characters',
        roleRequired: 'Please select role',
        avatarFormatError: 'Only JPG, JPEG, PNG formats are supported',
        avatarSizeError: 'Image size cannot exceed 2MB'
      },
      messages: {
        deleteConfirm: 'Are you sure you want to delete user "{name}"?',
        deleteTitle: 'Delete Confirmation',
        deleteSuccess: 'Deleted successfully',
        createSuccess: 'Created successfully',
        updateSuccess: 'Updated successfully',
        avatarUploadSuccess: 'Avatar uploaded successfully'
      }
    },
    actionPanel: {
      title: 'Action Panel',
      userManagement: 'User Management',
      userManagementDesc: 'Manage system users',
      workflowManagement: 'Workflow Management',
      workflowManagementDesc: 'Configure and manage workflows',
      redemptionManagement: 'Redemption Code',
      redemptionManagementDesc: 'Generate and manage redemption codes',
      announcement: 'Site Announcement',
      announcementDesc: 'Publish and manage site announcements',
      clickedAction: 'Clicked {action}'
    },
    redemption: {
      title: 'Redemption Code Management',
      create: 'Create Code',
      search: {
        keyword: 'Enter redemption code keyword',
        status: 'Status',
        query: 'Query',
        reset: 'Reset'
      },
      table: {
        id: 'ID',
        code: 'Redemption Code',
        credits: 'Credits',
        status: 'Status',
        usedBy: 'Used By ID',
        usedTime: 'Used Time',
        expireTime: 'Expire Time',
        description: 'Description',
        createTime: 'Create Time',
        actions: 'Actions',
        edit: 'Edit',
        delete: 'Delete'
      },
      status: {
        valid: 'Valid',
        used: 'Used',
        disabled: 'Disabled'
      },
      dialog: {
        createTitle: 'Create Redemption Code',
        editTitle: 'Edit Redemption Code',
        credits: 'Credits',
        prefix: 'Prefix',
        prefixPlaceholder: 'Optional, e.g. VIP-',
        length: 'Length',
        expireTime: 'Expire Time',
        expireTimePlaceholder: 'Select expire time (optional)',
        description: 'Description',
        descriptionPlaceholder: 'Enter description (optional)',
        cancel: 'Cancel',
        createBtn: 'Create',
        saveBtn: 'Save',
        codeLabel: 'Redemption Code',
        statusLabel: 'Status',
        statusPlaceholder: 'Select status',
        usedNotEditable: 'Already used, cannot modify'
      },
      validation: {
        creditsRequired: 'Please enter credits',
        creditsMustBeNumber: 'Credits must be a number',
        creditsNotNegative: 'Credits cannot be negative',
        prefixMaxLength: 'Prefix cannot exceed 16 characters',
        lengthRequired: 'Please enter length',
        lengthNotEmpty: 'Length cannot be empty',
        lengthMin: 'Length must be at least 4',
        lengthMax: 'Length cannot exceed 64',
        descriptionMaxLength: 'Description cannot exceed 200 characters',
        statusRequired: 'Please select status'
      },
      messages: {
        loadFailed: 'Load failed',
        deleteConfirm: 'Are you sure you want to delete this redemption code?',
        deleteSuccess: 'Deleted successfully',
        deleteFailed: 'Delete failed',
        createSuccess: 'Created successfully',
        createFailed: 'Create failed',
        saveSuccess: 'Saved successfully',
        saveFailed: 'Save failed'
      }
    },
    workflow: {
      title: 'Workflow Management',
      create: 'Create Workflow',
      categoryManagement: 'Category Management',
      search: {
        keyword: 'Search workflow name',
        category: 'Select category',
        allCategories: 'All Categories',
        query: 'Search',
        reset: 'Reset'
      },
      table: {
        cover: 'Cover',
        id: 'ID',
        name: 'Name',
        description: 'Description',
        category: 'Category',
        credits: 'Credits Deducted',
        actions: 'Actions',
        edit: 'Edit',
        delete: 'Delete'
      },
      dialog: {
        createTitle: 'Create Workflow',
        editTitle: 'Edit Workflow',
        categoryTitle: 'Category Management',
        createCategoryTitle: 'Add Category',
        basicInfo: 'Basic Information',
        name: 'Name',
        namePlaceholder: 'Please enter workflow name',
        cover: 'Cover Image',
        coverPlaceholder: 'Enter image URL or upload',
        upload: 'Upload',
        description: 'Description',
        descriptionPlaceholder: 'Optional: workflow description',
        category: 'Category',
        categoryPlaceholder: 'Select category',
        credits: 'Credits Deducted',
        parseWorkflow: 'Upload and Parse Workflow JSON',
        chooseFile: 'Choose File',
        parsed: 'Parsed',
        parseSuccess: 'Parsed successfully',
        parseSummary: 'Identified configurable input nodes: {formNodes}, all nodes: {allNodes}',
        inputConfig: 'Input Node Configuration',
        outputConfig: 'Output Node Configuration',
        addOutput: 'Add Output',
        removeOutput: 'Remove',
        selectNode: 'Select node',
        selectType: 'Select type',
        nodeLabel: '{tips} ({key})',
        unnamedNode: 'Unnamed Node',
        enableForm: 'Set Form Component',
        required: 'Required',
        optional: 'Optional',
        formLabel: 'Form Label/Hint',
        defaultTemplate: 'Default Template (optional)',
        sizeLength: 'Size/Length',
        options: 'Options',
        optionsPlaceholder: 'Options (JSON object string, e.g. {"low":"Low","mid":"Mid"})',
        inputField: 'Input Field',
        cancel: 'Cancel',
        save: 'Save',
        categoryName: 'Category Name',
        categoryNamePlaceholder: 'Please enter category name',
        createCategory: 'Add Category',
        confirm: 'Confirm',
        coverUploadSuccess: 'Cover uploaded successfully'
      },
      formTypes: {
        TEXT_PROMPT: 'Text Input',
        RADIO_SELECTOR: 'Radio',
        CHECKBOX_SELECTOR: 'Checkbox',
        IMAGE_UPLOAD: 'Image Upload',
        IMAGE_SCRIBBLE: 'Image Scribble',
        IMAGE_CONFIGURABLE: 'Image Type (select specific control)',
        VIDEO_UPLOAD: 'Video Upload',
        AUDIO_UPLOAD: 'Audio Upload',
        TEXT_CONFIGURABLE: 'Text Type (select specific control)'
      },
      outputTypes: {
        image: 'Image',
        video: 'Video',
        audio: 'Audio'
      },
      validation: {
        nameRequired: 'Please enter workflow name',
        nameLength: 'Name length between 1 and 100 characters',
        categoryRequired: 'Please select a category',
        creditsRequired: 'Please enter credits deducted',
        creditsMin: 'Credits cannot be less than 0',
        categoryNameRequired: 'Please enter category name',
        categoryNameLength: 'Length between 1 and 50 characters',
        fileFormatError: 'Please upload a .json file',
        optionsRequired: 'Selector type must provide options',
        optionsInvalidFormat: 'Options must be a non-empty JSON object',
        optionsJsonError: 'Options must be a JSON object string'
      },
      messages: {
        deleteConfirm: 'Confirm delete "{name}"? This will not delete generated works, only form and output configuration.',
        deleteTitle: 'Delete Confirmation',
        deleteSuccess: 'Deleted successfully',
        updateSuccess: 'Updated successfully',
        saveSuccess: 'Workflow configuration saved successfully',
        categoryCreateSuccess: 'Category created successfully',
        categorySaveSuccess: 'Saved successfully',
        categoryDeleteConfirm: 'Confirm delete category "{name}"?',
        categoryDeleteSuccess: 'Deleted successfully',
        cancel: 'Cancel'
      }
    },
    announcement: {
      title: 'Site Announcement',
      publishButton: 'Publish Announcement',
      clearButton: 'Clear Announcement',
      dialogTitle: 'Publish Announcement',
      publisher: 'Publisher',
      publisherLabel: 'Publisher',
      publisherPlaceholder: 'Enter publisher name',
      form: {
        title: 'Title',
        titlePlaceholder: 'Enter announcement title',
        publisher: 'Publisher',
        publisherPlaceholder: 'Enter publisher name',
        content: 'Content',
        contentPlaceholder: 'Enter announcement content',
        time: 'Time (Optional)',
        timePlaceholder: 'Select publish time (current time if not selected)'
      },
      validation: {
        titleRequired: 'Please enter title',
        titleMaxLength: 'Title cannot exceed 100 characters',
        publisherRequired: 'Please enter publisher',
        publisherMaxLength: 'Publisher cannot exceed 50 characters',
        contentRequired: 'Please enter content',
        contentMaxLength: 'Content cannot exceed 1000 characters'
      },
      messages: {
        publishSuccess: 'Published successfully',
        clearConfirm: 'Are you sure you want to clear the current site announcement?',
        clearTitle: 'Clear Confirmation',
        clearSuccess: 'Announcement cleared'
      },
      empty: {
        title: 'No site announcement',
        hint: 'Click "Publish Announcement" in the top right corner to publish'
      }
    }
  },
  profile: {
    dialogs: {
      changeAvatar: 'Change Avatar',
      editNickname: 'Edit Nickname'
    },
    banner: {
      avatarAlt: 'User Avatar',
      changeAvatar: 'Change Avatar',
      defaultNickname: 'No Nickname Set',
      edit: 'Edit',
      availableCredits: 'Available Credits',
      frozenCredits: 'Frozen Credits',
      totalCredits: 'Total Credits'
    },
    transactions: {
      title: 'Credit Transactions',
      refresh: 'Refresh',
      selectType: 'Select Transaction Type',
      allTransactions: 'All Transactions',
      recharge: 'Recharge',
      consume: 'Consume',
      refund: 'Refund',
      freeze: 'Freeze',
      loading: 'Loading...',
      noRecords: 'No transaction records',
      allLoaded: 'All records loaded',
      fetchError: 'Failed to fetch transactions'
    },
    avatar: {
      selectImage: 'Click to select avatar image',
      formatHint: 'Supports JPG, PNG formats, file size up to 2MB',
      preview: 'Preview Image',
      cancel: 'Cancel',
      uploading: 'Uploading...',
      upload: 'Confirm Upload',
      formatError: 'Please select JPG or PNG format image',
      sizeError: 'Image size cannot exceed 2MB',
      invalidFile: 'Please select a valid image file',
      uploadSuccess: 'Your avatar has been successfully updated!',
      uploadFailed: 'Avatar upload failed'
    },
    nickname: {
      currentLabel: 'Current Nickname',
      notSet: 'Not Set',
      newLabel: 'New Nickname',
      placeholder: 'Enter new nickname',
      rulesTitle: 'Nickname Rules',
      rule1: 'Length: 2-20 characters',
      rule2: 'Can contain Chinese, English, numbers',
      rule3: 'Cannot contain special symbols',
      rule4: 'Cannot be pure numbers',
      cancel: 'Cancel',
      saving: 'Saving...',
      save: 'Save',
      required: 'Please enter nickname',
      lengthError: 'Nickname length should be 2-20 characters',
      formatError: 'Nickname can only contain Chinese, English, numbers, and cannot be pure numbers',
      sameError: 'New nickname cannot be the same as current nickname',
      invalidError: 'Please enter a valid nickname',
      updateSuccess: 'Nickname updated successfully',
      updateFailed: 'Nickname update failed'
    }
  },
  works: {
    title: 'My Works',
    noMore: 'All works displayed',
    fetchError: 'Failed to fetch works list',
    taskCompleted: 'Task completed, refreshing works list',
    batchSelect: 'Select',
    selectAll: 'Select All',
    selectedCount: '{count} selected',
    batchDelete: 'Batch Delete',
    cancel: 'Cancel',
    noWorksSelected: 'Please select works to delete',
    batchDeleteConfirm: 'Are you sure you want to delete {count} selected works? This action cannot be undone.',
    batchDeleteTitle: 'Confirm Batch Delete',
    confirmDelete: 'Confirm Delete',
    batchDeleteSuccess: 'Successfully deleted {count} works',
    batchDeleteFailed: 'Batch delete failed, please try again',
    banner: {
      title: 'My Creative Portfolio',
      description: 'Explore the infinite possibilities of AI creativity, where every piece is a perfect blend of inspiration and technology',
      stats: {
        aiCreation: 'AI Creation',
        highQuality: 'High Quality',
        permanentSave: 'Permanent Save'
      }
    },
    empty: {
      title: 'No works yet',
      description: 'Start creating your first work',
      createButton: 'Create Work'
    },
    model3D: {
      noModel: 'No 3D model available',
      loading: 'Loading 3D model...',
      alt: '3D Model'
    },
    workCard: {
      videoNotSupported: 'Your browser does not support video playback',
      audioNoPreview: 'Audio files do not support preview',
      loadFailed: 'Failed to load work',
      workPrefix: 'Work',
      type: {
        image: 'Image',
        video: 'Video',
        audio: 'Audio',
        model: 'Model'
      },
      time: {
        justNow: 'Just now',
        minutesAgo: 'minutes ago',
        hoursAgo: 'hours ago'
      }
    }
  },
  
  // Utility functions
  utils: {
    // Time grouping
    timeGroup: {
      today: 'Today',
      yesterday: 'Yesterday',
      thisWeek: 'This Week',
      lastWeek: 'Last Week',
      thisMonth: 'This Month',
      lastMonth: 'Last Month',
      earlier: 'Earlier'
    },
    
    // Network request errors
    request: {
      networkDisconnected: 'Network disconnected, please check your internet connection',
      networkRequestFailed: 'Network request failed, please check your internet connection',
      unauthorizedWarning: 'Session expired, please log in again',
      requestFailed: 'Request failed',
      requestError: 'An error occurred, please try again later',
      badRequest: 'Invalid request parameters',
      unauthorized: 'Session expired, please log in again',
      forbidden: 'Access denied',
      notFound: 'The requested resource was not found',
      internalServerError: 'Internal server error',
      serverResponseFailed: 'Server response failed',
      serverNoResponse: 'Server not responding, please check your internet connection',
      networkConnectionFailed: 'Network connection failed, please check your internet connection',
      requestTimeout: 'Request timeout, please check your internet connection and try again'
    }
  },

  layouts: {
    // Sidebar menu
    sidebar: {
      menu: {
        onlineGeneration: 'Create',
        onlineGenerationDesc: 'ComfyUI Workflow',
        myWorks: 'My Works',
        profile: 'Profile',
        aiChat: 'AI Chat',
        aiChatBadge: 'Free',
        redemptionCode: 'Redeem Code',
        systemManagement: 'System',
        sectionAI: 'AI',
        sectionOther: 'Other'
      },
      footer: {
        announcement: 'Announcements',
        visitGitHub: 'Visit GitHub',
        aboutUs: 'About Us'
      }
    },
    // Prompt text
    authRequired: {
      redemptionCode: 'Please log in to use the redemption code feature'
    }
  },
  
  about: {
    title: 'About Us',
    hero: {
      title: 'Conni-X-Pro',
      subtitle: 'AI Creative Generation Platform',
      description: 'Explore the infinite possibilities of AI, making creativity within reach'
    },
    project: {
      title: 'Project Introduction',
      description: 'Conni-X-Pro is a powerful AI creative generation platform that integrates ComfyUI workflow, multi-model AI chat, and more. We are committed to providing users with simple, easy-to-use, and powerful AI creation tools.',
      features: {
        title: 'Core Features',
        items: {
          workflow: {
            title: 'ComfyUI Workflow',
            desc: 'Powerful visual node editor supporting image, video, audio, and 3D model generation'
          },
          aiChat: {
            title: 'Multi-Model AI Chat',
            desc: 'Integrates multiple mainstream AI models, supporting multimodal inputs like text, images, and files'
          },
          management: {
            title: 'Work Management',
            desc: 'Permanently save your creations, view and manage your AI portfolio anytime'
          },
          system: {
            title: 'System Management',
            desc: 'Complete user management, workflow management, credit system, and other backend features'
          }
        }
      },
      tech: {
        title: 'Tech Stack',
        frontend: 'Vue 3 + TypeScript + Vite',
        ui: 'Element Plus + Custom Theme',
        backend: 'Node.js + Express',
        ai: 'ComfyUI + OpenAI Compatible API'
      }
    },
    team: {
      title: 'Team',
      members: {
        developer: {
          name: 'Developer',
          role: 'Full Stack Engineer',
          desc: 'Passionate about technology, focused on AI application development'
        }
      },
      contribute: 'Welcome to join us and build a better AI creation platform together!'
    },
    sponsor: {
      title: 'Support Us',
      description: 'If you find this project helpful, please consider sponsoring to support our development work',
      wechat: 'WeChat Pay',
      alipay: 'Alipay',
      thanks: 'Thank you for your support!'
    },
    links: {
      github: 'GitHub Repository',
      documentation: 'Documentation',
      contact: 'Contact Us'
    },
    footer: {
      license: 'License',
      version: 'Version'
    }
  }
}


