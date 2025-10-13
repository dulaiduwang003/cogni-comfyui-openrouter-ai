export namespace SystemOverviewApi {

    export interface ActiveModel {
        modelName: string;
        callCount: number;
      }
      
      export interface UserStats {
        totalUsers: number;
        onlineUsers: number;
        todayNewUsers: number;
      }
      
      export  interface AIStats {
        todayApiCalls: number;
        todayTokensUsed: number;
        todayConversations: number;
        activeModels: ActiveModel[];
      }
      
      export interface WorkflowStats {
        totalWorkflows: number;
        todayTasks: number;
        todaySuccessTasks: number;
        todayFailedTasks: number;
        todayCancelledTasks: number;
      }
      
      export interface SystemResources {
        cpuUsage: number;
        memoryUsage: number;
        memoryTotal: number;
        memoryUsed: number;
        memoryFree: number;
      }
      
      export interface TaskStats {
        queuedTasks: number;
        buildingTasks: number;
      }
      
    

    
    export interface Result {
        userStats: UserStats;
        aiStats: AIStats;
        workflowStats: WorkflowStats;
        systemResources: SystemResources;
        taskStats: TaskStats;
        timestamp: string;
    }

} 
